import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.models.TransactionModel

class TransactionAdapter(private var transactions: List<TransactionModel>,private val listener: OnItemClickListener) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    class TransactionViewHolder(itemView: View,listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position)
                }
            }
        }

        val titleTextView: TextView = itemView.findViewById(R.id.textView5)
        var amountTextView: TextView = itemView.findViewById(R.id.textView6)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_transaction, parent, false)
        return TransactionViewHolder(view,listener)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.titleTextView.text = transaction.title
        holder.amountTextView.text = "$${transaction.amount}"


        if (transaction.type=="credit" || transaction.amount==0.toFloat()) {
            holder.amountTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
        } else {
            holder.amountTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
        }
    }

    override fun getItemCount() = transactions.size

    fun updateTransactions(newTransactions: List<TransactionModel>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
