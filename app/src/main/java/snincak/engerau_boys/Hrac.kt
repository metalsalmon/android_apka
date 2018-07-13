package snincak.engerau_boys

import java.io.Serializable
import java.util.*

/**
 * Created by sninc on 01-Apr-18.
 */
data class Hrac(val id: Int, var meno: String, var priezvisko: String,var prezyvka: String, var cislo_dresu: Int, var vyska: Double,
                var vaha: Int, var registracia: String, var pozicia: Int, var fotka: String ): Serializable {
    constructor() : this(-1, "","","", 0, 0.0, 0, "",0,"")

}
