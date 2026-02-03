package moviles.pablohiguero.examen2evlistacompra.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView; // Importante
import androidx.core.content.ContextCompat; // Importante
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import moviles.pablohiguero.examen2evlistacompra.R;
import moviles.pablohiguero.examen2evlistacompra.models.Store;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    private List<Store> listData;
    private OnItemClickListener itemListener;

    public interface OnItemClickListener {
        void onItemClick(Store store, int position);
        void onItemLongClick(Store store, int position);
    }

    public StoreAdapter(List<Store> listData, OnItemClickListener itemListener) {
        this.listData = listData;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvStatus;
        CardView cardView; // Necesitamos referencia al CardView

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // El propio itemView es el CardView en item_store.xml
            cardView = (CardView) itemView;

            tvName = itemView.findViewById(R.id.tvStoreName);
            tvAddress = itemView.findViewById(R.id.tvStoreAddress);
            tvStatus = itemView.findViewById(R.id.tvStoreStatus);
        }

        public void assignData(Store store, OnItemClickListener listener) {
            tvName.setText(store.getName());
            tvAddress.setText(store.getAddress());
            Context context = itemView.getContext();

            if (store.isActive()) {
                // TIENDA ACTIVA: Fondo Indigo Suave + Etiqueta Visible
                tvStatus.setVisibility(View.VISIBLE);
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.activeStoreBackground));
                cardView.setCardElevation(8f); // Más elevación para destacar
            } else {
                // TIENDA INACTIVA: Fondo Blanco + Etiqueta Oculta
                tvStatus.setVisibility(View.GONE);
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.cardBackground));
                cardView.setCardElevation(2f);
            }


            itemView.setOnClickListener(v -> {
                if (getBindingAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(store, getBindingAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (getBindingAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(store, getBindingAdapterPosition());
                    return true;
                }
                return false;
            });
        }
    }
}