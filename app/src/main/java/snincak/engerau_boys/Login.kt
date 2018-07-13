package snincak.engerau_boys

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView

import java.util.ArrayList
import android.Manifest.permission.READ_CONTACTS
import android.content.Intent
import android.view.Window
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity(), LoaderCallbacks<Cursor> {

    private val meno= "adam"
    private val heslo="12345"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        btn_prihlas.setOnClickListener { attemptLogin() }
    }
    private fun attemptLogin() {
        email.error = null
        password.error = null

        val meno_str = email.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView: View? = null

        if (!isPasswordValid(passwordStr)) {
            password.error = getString(R.string.chybne_heslo)
            focusView = password
            cancel = true
        }

        if (TextUtils.isEmpty(meno_str)) {
            email.error = getString(R.string.error_povinne_meno)
            focusView = email
            cancel = true
        } else if (!isEmailValid(meno_str)) {
            email.error = getString(R.string.error_chybne_meno)
            focusView = email
            cancel = true
        }

        if (cancel) {

            focusView?.requestFocus()
        } else {
           val intent = Intent(this, Prehlad_hracov::class.java)
            startActivityForResult(intent,2)

        }
    }

    private fun isEmailValid(meno_over: String): Boolean {
        return meno_over.equals(meno);
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.equals(heslo)
    }


    override fun onLoaderReset(p0: Loader<Cursor>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Cursor> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLoadFinished(p0: Loader<Cursor>?, p1: Cursor?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBackPressed() {

    }
}
