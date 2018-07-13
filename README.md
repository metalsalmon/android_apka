# Engerau_boys


Aplikácia pre futbalový tím pozostáva po prihlásení zo zoznamu hráčov vybraného tímu Engerau Boys. 
Aplikácia umožňuje pridať nového hráča, vymazať aktuálneho hráča. Ďalej umožňuje zobraziť konkrétneho hráča aj s fotkou a detailnými informáciami, podporuje možnosť upraviť údaje zvoleného hráča, pridanie fotky hráča z galérie alebo priamo odfotiť.
Informácie o hráčoch sú uložené v databáze na serveri, fotky hráčov lokálne v pamäti telefónu.

### návrh obrazoviek
  ![obrazovky](https://user-images.githubusercontent.com/25955513/42688726-c55355b0-869d-11e8-9321-9ec96e887708.png)

### api

* Zobraz hráčov:    GET /hrac
* Zobraz detail:	  GET /hrac/:id
* Uprav hráča:	    PUT /hrac/:id
* Pridaj hráča:	 	  POST /hrac
* Vymaž hráča:      DELETE /hrac/:id
