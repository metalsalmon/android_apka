package snincak.engerau_boys

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ListAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_prehlad_hracov.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import snincak.engerau_boys.Hraci_list_adapter
import java.io.Serializable
import snincak.engerau_boys.Hrac



class Prehlad_hracov : Activity() {
    val list_hracov = arrayListOf<Hrac>()
    val adapter = Hraci_list_adapter(this, list_hracov)
    var pozicia: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_prehlad_hracov)

       loading(true)
        getni().execute(getString(R.string.get_hraci))

        hraci_list.onItemClickListener = AdapterView.OnItemClickListener() { adapterView, view, position, id ->

            loading(true)
            pozicia=position
            getni_jedneho().execute(getString(R.string.http_request)+list_hracov[position].id)

        }

        hraci_list.onItemLongClickListener = AdapterView.OnItemLongClickListener() { adapterView, view, position, id ->

            val dialogBuilder = AlertDialog.Builder(this@Prehlad_hracov)
            dialogBuilder.setTitle(R.string.zmazanie)
            dialogBuilder.setMessage(R.string.potvrdenie_zmazania)

            dialogBuilder.setPositiveButton(R.string.dialog_ok,
                    DialogInterface.OnClickListener { _, _ ->

                        pozicia=position
                        loading(true)
                        zmaz().execute(getString(R.string.http_request) + list_hracov[position].id)



                    })

            dialogBuilder.setNegativeButton(R.string.dialog_zrus, DialogInterface.OnClickListener { _, _ -> })


            val potvrd_zmazanie = dialogBuilder.create()
            potvrd_zmazanie.show()
            true
        }
        btn_pridaj_hraca.setOnClickListener {

            val intent = Intent(this, UpravHraca::class.java)
            startActivity(intent)
        }
    }


    inner class getni: AsyncTask<String, String, String>(){
        override fun doInBackground(vararg url: String?): String {

            var text: String =""
            var pripoj=true
            val pripojenie= URL(url[0]).openConnection() as HttpURLConnection

            try {
                    pripojenie.connect()
                    text = pripojenie.inputStream.use { it.reader().use { reader -> reader.readText() } }
                } catch (e: Exception) {
                    pripoj=false
                    e.printStackTrace()

                } finally {
                   if(pripoj.equals(true)) pripojenie.disconnect()
                }

          if(pripoj.equals(true))return text
            else return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
           if(!result.equals("")) spracujJson(result)
            else{
               loading(false)
               Toast.makeText(this@Prehlad_hracov,R.string.chyba_pripojenia, Toast.LENGTH_LONG).show()
               finish()
           }
        }
    }


    inner class getni_jedneho: AsyncTask<String, String, String>() {
        override fun doInBackground(vararg url: String?): String {
            var text: String
            text = ""
            var pripoj = true
            val pripojenie = URL(url[0]).openConnection() as HttpURLConnection
            try {
                pripojenie.connect()
                if(pripojenie.responseCode==404) return "404"
                text = pripojenie.inputStream.use { it.reader().use { reader -> reader.readText() } }
            } catch (e: Exception) {
                pripoj = false
                e.printStackTrace()
            } finally {
                if (pripoj.equals(true)) pripojenie.disconnect()
            }

            if (pripoj.equals(true)) {
               return text
            }
            return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(result.equals("404")){

                list_hracov.removeAt(pozicia)
                hraci_list.adapter = adapter
                Toast.makeText(this@Prehlad_hracov,R.string.hrac_neexistuje, Toast.LENGTH_SHORT).show()
                loading(false)
            }
            else if (!result.equals("")) spracuj_jedneho(result)
            else{
                loading(false)
                Toast.makeText(this@Prehlad_hracov,R.string.chyba_pripojenia, Toast.LENGTH_SHORT).show()

            }
        }
    }
        private fun spracuj_jedneho(text_json: String?) {
            val hrac_json = JSONObject(text_json)
            val hrac: Hrac
            hrac= Hrac(hrac_json.getInt("id"),
                    hrac_json.getString("meno"),
                    hrac_json.getString("priezvisko"),
                    hrac_json.getString("prezyvka"),
                    hrac_json.getInt("cislo_dresu"),
                    hrac_json.getDouble("vyska"),
                    hrac_json.getInt("vaha"),
                    hrac_json.getString("registracia"),
                    hrac_json.getInt("pozicia"),
                    hrac_json.getString("fotka")
            )
            loading(false)
            val intent = Intent(this, DetailHraca::class.java)
            intent.putExtra("hrac", hrac)
            startActivity(intent)
        }




    private fun spracujJson(text_json: String?) {
        try {
            val json_pole = JSONArray(text_json)


        var i = 0

        while (i < json_pole.length()) {
            val hrac = json_pole.getJSONObject(i)

            list_hracov.add(Hrac(hrac.getInt("id"), hrac.getString("meno"), hrac.getString("priezvisko"), "", hrac.getInt("cislo_dresu"), -1.0, -1, "",
                    -1, ""))
            i++
        }}catch (e: Exception){
            val hrac=org.json.JSONObject(text_json)
            list_hracov.add(Hrac(hrac.getInt("id"), hrac.getString("meno"), hrac.getString("priezvisko"), "", hrac.getInt("cislo_dresu"), -1.0, -1, "",
                    -1, ""))
          }

        loading(false)
        hraci_list.adapter = adapter
    }

    inner class zmaz: AsyncTask<String, String, String>(){
        override fun doInBackground(vararg url: String?): String {

            var text: String
            text=""

            var pripoj=true
            val pripojenie= URL(url[0]).openConnection() as HttpURLConnection

            pripojenie.requestMethod = "DELETE"

            try {
                pripojenie.connect()
                if(pripojenie.responseCode==404) return "404"
                text = pripojenie.inputStream.use { it.reader().use { reader -> reader.readText() } }
            } catch (e: Exception) {
                pripoj=false
                e.printStackTrace()

            } finally {
                if(pripoj.equals(true)) pripojenie.disconnect()
            }

            if(pripoj.equals(true)){
                return text
            }

            else return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            loading(false)
            if(!result.equals(""))
            {
                list_hracov.removeAt(pozicia)
                hraci_list.adapter = adapter
            }
            else{
                Toast.makeText(this@Prehlad_hracov,R.string.chyba_pripojenia, Toast.LENGTH_LONG).show()

            }
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun loading(show: Boolean) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()


           hraci_list.visibility = if (show) View.GONE else View.VISIBLE
            meno_hraca.visibility = if (show) View.GONE else View.VISIBLE
            cislo_hraca.visibility = if (show) View.GONE else View.VISIBLE
            btn_pridaj_hraca.visibility = if (show) View.GONE else View.VISIBLE

            hraci_list.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            hraci_list.visibility = if (show) View.GONE else View.VISIBLE
                            meno_hraca.visibility = if (show) View.GONE else View.VISIBLE
                            cislo_hraca.visibility = if (show) View.GONE else View.VISIBLE
                            btn_pridaj_hraca.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            cakanie_na_get.visibility = if (show) View.VISIBLE else View.GONE
            cakanie_na_get.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            cakanie_na_get.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            cakanie_na_get.visibility = if (show) View.VISIBLE else View.GONE
            hraci_list.visibility = if (show) View.GONE else View.VISIBLE
        }
    }


    override fun onBackPressed() {

        val dialogBuilder = AlertDialog.Builder(this@Prehlad_hracov)
        dialogBuilder.setTitle(R.string.odhlasenie)
        dialogBuilder.setMessage(R.string.potvrdenie_odhlasenia)

        dialogBuilder.setPositiveButton(R.string.dialog_ok,
                DialogInterface.OnClickListener { _, _ ->
                    if(getIntent().hasExtra("hrac")) {
                        getIntent().removeExtra("hrac")
                    }
                    val intent = Intent(this, Login::class.java)

                    setResult(2, intent)
                    startActivity(intent)
                    finish()

                })
        dialogBuilder.setNegativeButton(R.string.dialog_zrus,
                DialogInterface.OnClickListener { _, _ ->
                })


        dialogBuilder.create().show()


    }

}
