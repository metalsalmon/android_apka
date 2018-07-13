package snincak.engerau_boys

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.Window
import kotlinx.android.synthetic.main.activity_uprav_hraca.*
import java.net.HttpURLConnection
import java.net.URL
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.text.TextUtils
import android.util.Log
import android.view.View
import org.json.JSONObject
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_detail_hraca.*
import java.io.*
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*


class UpravHraca : Activity() {
        private var hrac: Hrac?=null
        private var aktivita: Int = 0

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_uprav_hraca)

            val spinner = findViewById<View>(R.id.vyber_pozicie) as Spinner
            val adapter = ArrayAdapter.createFromResource(this, R.array.pozicie_hracov, android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
                loading(false)

           if(getIntent().hasExtra("hrac")) {

               aktivita=1
               hrac = getIntent().getSerializableExtra("hrac") as Hrac

               vypln_polia(hrac!!)


               button_uloz.setOnClickListener({
                   uloz_na_server(1)
               })
           }else
           {
               hrac=Hrac()

               aktivita=2
               button_uloz.setOnClickListener({
                   uloz_na_server(2)
               })

           }

           uprav_fotka_hraca.setOnClickListener {


               val dialogBuilder = AlertDialog.Builder(this@UpravHraca)
               dialogBuilder.setTitle("fotka")
               dialogBuilder.setMessage("zvoľ akciu")

               dialogBuilder.setPositiveButton(R.string.dialog_fotak,
                       DialogInterface.OnClickListener { _, _ ->
                           val fotak = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                           if (fotak.resolveActivity(packageManager) != null) {
                               startActivityForResult(fotak,1)
                       }
                       })

               dialogBuilder.setNegativeButton(R.string.dialog_galeria,
                       DialogInterface.OnClickListener { _, _ ->
                           val galeria   = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                           if (galeria.resolveActivity(packageManager) != null) {
                               startActivityForResult(galeria,2)

                           }
                       })

               dialogBuilder.create().show()
        }

            val dateSetListener = object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker, rok: Int, mesiac: Int, den: Int)
                {
                    var kalendar = Calendar.getInstance()
                    kalendar.set(Calendar.YEAR, rok)
                    kalendar.set(Calendar.MONTH, mesiac)
                    kalendar.set(Calendar.DAY_OF_MONTH, den)
                    datum(kalendar)
                }
            }

            uprav_registracia.setOnClickListener(){
                var kalendar = Calendar.getInstance()
                DatePickerDialog(this@UpravHraca, dateSetListener, kalendar.get(Calendar.YEAR), kalendar.get(Calendar.MONTH), kalendar.get(Calendar.DAY_OF_MONTH)).show()
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null)
        {
        if (resultCode == RESULT_OK && (requestCode==1))
        {

            val fotka = data!!.extras!!.get("data") as Bitmap
            val cesta=uloz_fotku(fotka)
            if(cesta!="")
            {
                uprav_fotka_hraca!!.setImageBitmap(fotka)
                hrac!!.fotka=cesta.substringAfterLast("/")
            }
        }

       else  if (resultCode == RESULT_OK && (requestCode==2))
        {
                val contentURI = data!!.data
                try
                {
                    val fotka = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    val cesta = uloz_fotku(fotka)
                    if(cesta!="") {

                        uprav_fotka_hraca!!.setImageBitmap(fotka)
                        hrac!!.fotka=cesta.substringAfterLast("/")
                    }


                }
                catch (e: IOException) {
                    e.printStackTrace()
                   Toast.makeText(this@UpravHraca, "chybicka!", Toast.LENGTH_SHORT).show()
                }

            }
            else   super.onActivityResult(requestCode, resultCode, data)

        }else super.onActivityResult(requestCode, resultCode, data)
    }


    fun uloz_fotku(foto: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        foto.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val engerau_priecinok = File((Environment.getExternalStorageDirectory()).toString() + "/"+ packageName)

        if (!engerau_priecinok.exists()) engerau_priecinok.mkdirs()

        try
        {
            val fotka = File(engerau_priecinok, ((Calendar.getInstance().getTimeInMillis()).toString() + ".jpg"))
            fotka.createNewFile()
            val zapis = FileOutputStream(fotka)
            zapis.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this, arrayOf(fotka.getPath()), arrayOf("image/jpeg"), null)
            zapis.close()

            return fotka.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }


    inner class putni() : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg url: String?): String {
            var text: String
            text = ""
            var pripoj = true

            val pripojenie = URL(url[0]).openConnection() as HttpURLConnection
           // pripojenie.requestMethod = "PUT"
            pripojenie.requestMethod = url[1]
            pripojenie.setDoOutput(true);
            pripojenie.setDoInput(true);
            pripojenie.setRequestProperty("Content-Type", "application/json");
            pripojenie.setRequestProperty("Accept", "application/json");

          val json = "{\"meno\":\""+uprav_meno.text+"\",\"priezvisko\":\""+uprav_priezvisko.text+"\",\"prezyvka\":\""+uprav_prezyvka.text+"\",\"cislo_dresu\": "+uprav_cislo_dresu.text.toString()+",\"vyska\": "+uprav_vyska.text.toString()+", \"vaha\": "+uprav_vaha.text.toString()+", \"registracia\":\""+uprav_registracia.text+"\", \"pozicia\": "+(vyber_pozicie.getSelectedItemPosition().toInt()+1)+", \"fotka\":\""+hrac!!.fotka+"\"}"

            try {
                val out = OutputStreamWriter(pripojenie.getOutputStream())
                out.write(json)
                out.close()
            }catch (e:Exception){
                return "nepripojene"
            }
            try {
                pripojenie.connect()
                if(pripojenie.responseCode==404) return "404"
                text = pripojenie.inputStream.use { it.reader().use { reader -> reader.readText() } }
            } catch (ee: Exception) {
                pripoj = false
                ee.printStackTrace()
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
            loading(false)
            if(result.equals("404")){
                Toast.makeText(this@UpravHraca,"hráč už neexistuje", Toast.LENGTH_SHORT).show()
            }
            else if(result.equals("nepripojene")){
                Toast.makeText(this@UpravHraca,"chyba pripojenia!", Toast.LENGTH_SHORT).show()
            }
            else if (!result.equals("")) {

                val hrac_json = JSONObject(result)

                hrac!!.meno=hrac_json.getString("meno")
                hrac!!.priezvisko=hrac_json.getString("priezvisko")
                hrac!!.prezyvka= hrac_json.getString("prezyvka")
                hrac!!.cislo_dresu=hrac_json.getInt("cislo_dresu")
                hrac!!.vyska=hrac_json.getDouble("vyska")
                hrac!!.vaha=hrac_json.getInt("vaha")
                hrac!!.registracia=hrac_json.getString("registracia")
                hrac!!.pozicia=hrac_json.getInt("pozicia")
                hrac!!.fotka=hrac_json.getString("fotka")

                if(getIntent().hasExtra("hrac")) {
                    getIntent().removeExtra("hrac")
                }
                if(aktivita==2)
                {
                    val intent = Intent(this@UpravHraca, DetailHraca::class.java)
                    intent.putExtra("hrac", hrac)
                    startActivity(intent)
                }
                else {
                    val intent = Intent()
                    intent.putExtra("hrac_upraveny", hrac!!)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    fun vypln_polia(hrac: Hrac){

        val existuje_obrazok = File(Environment.getExternalStorageDirectory().toString() + "/"+ packageName+"/"+hrac!!.fotka)

        if(existuje_obrazok.exists())
        {
            uprav_fotka_hraca.setImageURI(Uri.fromFile(existuje_obrazok))

        }
        uprav_meno.append(hrac.meno)
        uprav_priezvisko.append(hrac.priezvisko)
        uprav_prezyvka.setText(hrac.prezyvka)
        uprav_cislo_dresu.append(hrac.cislo_dresu.toString())
        uprav_vyska.append(hrac.vyska.toString())
        uprav_vaha.append(hrac.vaha.toString())
        uprav_registracia.append(hrac.registracia.toString())

        var poz: String
        poz=""
        when (hrac.pozicia) {
            1 -> vyber_pozicie.setSelection(0)
            2 -> vyber_pozicie.setSelection(1)
            3 -> vyber_pozicie.setSelection(2)
            4 -> vyber_pozicie.setSelection(3)
        }
    }

    override fun onBackPressed() {

        if(getIntent().hasExtra("hrac")) {
            getIntent().removeExtra("hrac")
        }
        val intent = Intent()
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }

    fun datum(kalendar: Calendar)
    {
        val format = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(format, Locale.US)
        uprav_registracia!!.setText(sdf.format(kalendar.getTime()))
    }

    fun uloz_na_server(metoda: Int)
    {
        uprav_meno.error = null
        uprav_priezvisko.error = null
        uprav_prezyvka.error=null
        uprav_cislo_dresu.error = null
        uprav_vaha.error = null
        uprav_vyska.error = null

        val meno = uprav_meno.text.toString()
        val priezvisko = uprav_priezvisko.text.toString()
        val prezyvka = uprav_prezyvka.text.toString()

        var cislo_dresu: Int
        if(uprav_cislo_dresu.text.toString().equals("")){cislo_dresu=-1}
        else {cislo_dresu = uprav_cislo_dresu.text.toString().toInt()}

        var vaha:Int
        if(uprav_vaha.text.toString().equals("")){vaha=-1}
        else {vaha = uprav_vaha.text.toString().toInt()}

        var vyska: Double
        if(uprav_vyska.text.toString().equals("")){vyska=-1.0}
        else {vyska= uprav_vyska.text.toString().toDouble()}

        val registracia = uprav_registracia.text.toString()

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(registracia)) {
            uprav_registracia.error = getString(R.string.error_registracia)
            focusView = uprav_registracia
            cancel = true
        }
        if (vyska > 2.30 || vyska < 1) {
            uprav_vyska.error = getString(R.string.error_vyska)
            focusView = uprav_vyska
            cancel = true
        }

        if (vaha > 130 || vaha < 40) {
            uprav_vaha.error = getString(R.string.error_vaha)
            focusView = uprav_vaha
            cancel = true
        }
        if (cislo_dresu > 99 || cislo_dresu < 1) {
            uprav_cislo_dresu.error = getString(R.string.error_cislo_dresu)
            focusView = uprav_cislo_dresu
            cancel = true
        }
        if (TextUtils.isEmpty(prezyvka)) {
            uprav_prezyvka.error = getString(R.string.error_prezyvka)
            focusView = uprav_prezyvka
            cancel = true
        }
        if (TextUtils.isEmpty(priezvisko)) {
            uprav_priezvisko.error = getString(R.string.error_priezvisko)
            focusView = uprav_priezvisko
            cancel = true
        }
        if (TextUtils.isEmpty(meno)) {
            uprav_meno.error = getString(R.string.error_meno)
            focusView = uprav_meno
            cancel = true
        }

        if (cancel) {

            focusView?.requestFocus()
        }else{
            loading(true)
            if(metoda==1) putni().execute(getString(R.string.http_request) + hrac!!.id,"PUT")
            else
            {
                if(hrac!!.fotka=="") hrac!!.fotka="predvolena.jpg"
                putni().execute(getString(R.string.http_request), "POST")
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun loading(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()


            schovaj.visibility = if (show) View.GONE else View.VISIBLE

            schovaj.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            schovaj.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            nacitavam.visibility = if (show) View.VISIBLE else View.GONE
            nacitavam.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            nacitavam.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            nacitavam.visibility = if (show) View.VISIBLE else View.GONE
            schovaj.visibility = if (show) View.GONE else View.VISIBLE
        }
    }
}
