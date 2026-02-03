package moviles.pablohiguero.examen2evlistacompra.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import moviles.pablohiguero.examen2evlistacompra.R;
import moviles.pablohiguero.examen2evlistacompra.models.Item;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> listData;
    private OnItemAction itemListener;

    public interface OnItemAction {
        void onAddClick(Item item, int position);
        void onRemoveClick(Item item, int position);
        void onItemClick(Item item, int position);
    }

    public ItemAdapter(List<Item> listData, OnItemAction itemListener) {
        this.listData = listData;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.assignData(listData.get(position), itemListener);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory, tvPrice, tvQuantity, tvPurchasedLabel;
        ImageView btnPlus, btnMinus;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvCategory = itemView.findViewById(R.id.tvProductCategory);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvPurchasedLabel = itemView.findViewById(R.id.tvPurchasedLabel);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            cardView = itemView.findViewById(R.id.cardViewProduct);
        }

        public void assignData(Item item, OnItemAction listener) {
            tvName.setText(item.getName());
            tvCategory.setText(item.getCategory());
            tvPrice.setText(String.format("%.2f €", item.getPrice()));
            tvQuantity.setText(String.valueOf(item.getQuantity()));

            Context context = itemView.getContext();

            // LOGICA CRÍTICA DEL EXAMEN:
            // Leemos directamente del objeto Item si está comprado o no
            if (item.isPurchased()) {
                // MARCADO: Fondo Verdoso suave (Teal)
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.selectedItem));
                cardView.setCardElevation(0f);
                tvPurchasedLabel.setVisibility(View.VISIBLE);
                // Opcional: Poner un check o cambiar texto de color
            } else {
                // NORMAL: Blanco
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.cardBackground));
                cardView.setCardElevation(2f);
                tvPurchasedLabel.setVisibility(View.GONE);
            }

            // Botón Sumar
            btnPlus.setOnClickListener(v -> {
                if (getBindingAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onAddClick(item, getBindingAdapterPosition());
                }
            });


            btnMinus.setOnClickListener(v -> {
                if (getBindingAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onRemoveClick(item, getBindingAdapterPosition());
                }
            });

            itemView.setOnClickListener(v -> {
                if (getBindingAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(item, getBindingAdapterPosition());
                }
            });
        }
    }
}