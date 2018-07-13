package snincak.engerau_boys

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

/**
 * Created by sninc on 01-Apr-18.
 */
class Hraci_list_adapter(val context: Context, val list:ArrayList<Hrac>) : BaseAdapter() {
    override fun getView(pozicia: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.prehlad_hracov_riadky,parent,false)

        val hrac_meno = view.findViewById<View>(R.id.hrac_meno) as AppCompatTextView
        val hrac_cislo = view.findViewById<View>(R.id.hrac_cislo) as AppCompatTextView
        hrac_meno.text = list[pozicia].meno+" "+list[pozicia].priezvisko
        hrac_cislo.text = list[pozicia].cislo_dresu.toString()

        return view

    }

    override fun getItem(pozicia: Int): Any {
        return list[pozicia]
            }


    override fun getItemId(pozicia: Int): Long {
        return pozicia.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }


}