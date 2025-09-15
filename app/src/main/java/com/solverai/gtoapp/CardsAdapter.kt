class CardsAdapter(
    private val cards: MutableList<String>,
    private val onRemove: (Int) -> Unit
) : RecyclerView.Adapter<CardsAdapter.CardViewHolder>() {

    class CardViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val cardImage: ImageView = view.findViewById(R.id.cardImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun getItemCount(): Int = cards.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val code = cards[position].lowercase()
        val resId = holder.view.context.resources.getIdentifier(
            "card_$code", "drawable", holder.view.context.packageName
        )
        holder.cardImage.setImageResource(resId)

        holder.itemView.setOnClickListener {
            AlertDialog.Builder(holder.view.context)
                .setTitle("Eliminar carta")
                .setMessage("¿Deseas eliminar esta carta?")
                .setPositiveButton("Sí") { _, _ -> onRemove(position) }
                .setNegativeButton("No", null)
                .show()
        }
    }
}
