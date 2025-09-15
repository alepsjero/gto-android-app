private fun openCardDialog() {
    val dialogView = layoutInflater.inflate(R.layout.dialog_select_card, null)
    val gridRanks = dialogView.findViewById<GridView>(R.id.gridRanks)
    val gridSuits = dialogView.findViewById<GridView>(R.id.gridSuits)

    val ranks = listOf("A","K","Q","J","T","9","8","7","6","5","4","3","2")
    val suits = listOf("C","P","T","D") // Corazón, Pica, Trébol, Diamante

    gridRanks.adapter = ImageAdapter(this, ranks.map { "rank_$it" })
    gridSuits.adapter = ImageAdapter(this, suits.map { "suit_$it" })

    val selected = mutableMapOf<String, String>()

    val builder = AlertDialog.Builder(this)
        .setView(dialogView)
        .setTitle("Seleccionar carta")
        .setNegativeButton("Cancelar", null)

    val dialog = builder.create()

    gridRanks.setOnItemClickListener { _, _, position, _ ->
        selected["rank"] = ranks[position]
        Toast.makeText(this, "Elegiste ${ranks[position]}", Toast.LENGTH_SHORT).show()
    }

    gridSuits.setOnItemClickListener { _, _, position, _ ->
        selected["suit"] = suits[position]
        if (selected.containsKey("rank")) {
            val card = selected["rank"]!! + selected["suit"]!!
            if (!cardEntries.contains(card)) {
                cardEntries.add(card)
                cardsAdapter.notifyDataSetChanged()
            }
            dialog.dismiss()
        } else {
            Toast.makeText(this, "Primero elige un valor", Toast.LENGTH_SHORT).show()
        }
    }

    dialog.show()
}
