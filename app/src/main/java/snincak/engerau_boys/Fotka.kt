package snincak.engerau_boys

import android.app.Activity
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import kotlinx.android.synthetic.main.activity_detail_hraca.*
import kotlinx.android.synthetic.main.activity_fotka.*
import java.io.File
import android.content.Intent
import android.os.Environment


class Fotka : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_fotka)

        val intent = intent
        val foto = intent.getStringExtra("foto")

        val existuje_obrazok = File(Environment.getExternalStorageDirectory().toString() + "/"+ packageName+"/"+foto)

        if(existuje_obrazok.exists())
        {
            fotka.setImageURI(Uri.fromFile(existuje_obrazok));
        }else
        {
            val foto = Uri.parse("android.resource://snincak.engerau_boys/drawable/engerau")
            fotka.setImageURI(foto)
        }
    }
}
