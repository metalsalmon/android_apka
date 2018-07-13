package snincak.engerau_boys

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_detail_hraca.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.widget.ImageView
import java.io.File
import java.nio.file.Files.exists
import java.security.AccessController.getContext


class DetailHraca : Activity() {

    private var hrac: Hrac?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_detail_hraca)

        if(getIntent().hasExtra("hrac")) hrac= getIntent().getSerializableExtra("hrac") as Hrac;    //OSETRI AK NENI EXTRA

        fotka_hraca.setOnClickListener {
            klik_na_fotku()
        }

        button_uprav.setOnClickListener({
            val intent = Intent(this, UpravHraca::class.java)
            intent.putExtra("hrac", hrac)
            startActivityForResult(intent,1)
        })
        nastav_parametre()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                hrac= data.getSerializableExtra("hrac_upraveny") as Hrac
                getIntent().putExtra("hrac",hrac)
                nastav_parametre()
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    private  fun klik_na_fotku(){

        val intent = Intent(this, Fotka::class.java)
        intent.putExtra("foto", hrac!!.fotka)
        startActivity(intent)

    }
    private fun nastav_parametre() {


         val existuje_obrazok =File(Environment.getExternalStorageDirectory().toString() + "/"+ packageName+"/"+hrac!!.fotka)

        if(existuje_obrazok.exists())
        {
            fotka_hraca.setImageURI(Uri.fromFile(existuje_obrazok))

        }else
        {
           val omg = Uri.parse("android.resource://snincak.engerau_boys/drawable/engerau")
           fotka_hraca.setImageURI(omg)
        }


        meno_priezvisko.text=hrac!!.meno+" "+hrac!!.priezvisko
        prezyvka.text=hrac!!.prezyvka
        cislo_dresu.text=hrac!!.cislo_dresu.toString()
        vyska.text=hrac!!.vyska.toString()+" m"
        vaha.text=hrac!!.vaha.toString()+" kg"
        registracia.text=hrac!!.registracia.toString()

        var poz: String
        poz=""
        when (hrac!!.pozicia) {
            1 -> poz=R.string.zaloznik.toString()
            2 -> poz=R.string.obranca.toString()
            3 -> poz=R.string.utocnik.toString()
            4 -> poz=R.string.brankar.toString()
        }

        pozicia.text=poz

    }
    override fun onBackPressed() {

        if(getIntent().hasExtra("hrac")) {
            getIntent().removeExtra("hrac")
        }
        val intent = Intent(this, Prehlad_hracov::class.java)
        startActivity(intent)
        finish()
    }

}
