package moviles.pablohiguero.examen2evlistacompra.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import moviles.pablohiguero.examen2evlistacompra.R;
import moviles.pablohiguero.examen2evlistacompra.adapters.ItemAdapter;
import moviles.pablohiguero.examen2evlistacompra.models.Item;
import moviles.pablohiguero.examen2evlistacompra.models.Store;
import moviles.pablohiguero.examen2evlistacompra.utils.Utils;


public class SummaryFragment extends Fragment {

    private RecyclerView recycler;
    private TextView tvTotal, tvSummaryStore, tvCountProducts, tvCountUnits, tvEmptySummary;
    private Button btnShare, btnClear;
    private Realm realm;
    private ItemAdapter adapter;
    private Store activeStore;

    // Refrescar lista si añaden cosas en el otro fragment
    private final RealmChangeListener<Realm> realmListener = new RealmChangeListener<Realm>() {
        @Override
        public void onChange(Realm realm) {
            loadSummary();
        }
    };

    public SummaryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        realm = Realm.getDefaultInstance();
        realm.addChangeListener(realmListener);

        recycler = view.findViewById(R.id.recyclerSummary);
        tvSummaryStore = view.findViewById(R.id.tvSummaryStore);
        tvTotal = view.findViewById(R.id.tvSummaryTotal);
        tvCountProducts = view.findViewById(R.id.tvCountProducts);
        tvCountUnits = view.findViewById(R.id.tvCountUnits);
        tvEmptySummary = view.findViewById(R.id.tvEmptySummary);
        btnShare = view.findViewById(R.id.btnShare);
        btnClear = view.findViewById(R.id.btnClear);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // BOTÓN LIMPIAR: Pone cantidades a 0 y purchased a false
        btnClear.setOnClickListener(v -> {
            activeStore = realm.where(Store.class).equalTo("isActive", true).findFirst();
            if (activeStore != null) {
                realm.executeTransaction(r -> {
                    for (Item item : activeStore.getItems()) {
                        item.setQuantity(0);
                        item.setPurchased(false);
                    }
                });
                Toast.makeText(getContext(), "Carrito vaciado", Toast.LENGTH_SHORT).show();
                loadSummary();
            }
        });

        // BOTÓN COMPARTIR: Email
        btnShare.setOnClickListener(v -> {
            activeStore = realm.where(Store.class).equalTo("isActive", true).findFirst();
            if (activeStore != null) {
                List<Item> itemsComprados = activeStore.getItems().where().greaterThan("quantity", 0).findAll();


                // if (itemsComprados.isEmpty()) {
                //    Toast.makeText(getContext(), "La lista está vacía", Toast.LENGTH_SHORT).show();
                //    return;
                // }

                String body = Utils.buildShoppingListEmailBody(activeStore, itemsComprados);

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Lista de compra: " + activeStore.getName());
                intent.putExtra(Intent.EXTRA_TEXT, body);

                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "No tienes app de correo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadSummary();
        return view;
    }

    private void loadSummary() {
        activeStore = realm.where(Store.class).equalTo("isActive", true).findFirst();

        if (activeStore != null) {
            tvSummaryStore.setText("Resumen: " + activeStore.getName());

            // Filtrar items con cantidad > 0
            RealmResults<Item> selectedItems = activeStore.getItems().where().greaterThan("quantity", 0).findAll();

            adapter = new ItemAdapter(selectedItems, new ItemAdapter.OnItemAction() {
                @Override
                public void onAddClick(Item item, int position) {
                    realm.executeTransaction(r -> item.setQuantity(item.getQuantity() + 1));
                    // notify no es necesario aquí porque el RealmChangeListener recarga todo al detectar cambio
                }

                @Override
                public void onRemoveClick(Item item, int position) {
                    realm.executeTransaction(r -> {
                        if(item.getQuantity() > 0) item.setQuantity(item.getQuantity() - 1);
                        if (item.getQuantity() == 0) item.setPurchased(false);
                    });
                }

                @Override
                public void onItemClick(Item item, int position) {
                    // LOGICA DE MARCADO (SELECCIÓN VISUAL)
                    realm.executeTransaction(r -> {
                        item.setPurchased(!item.isPurchased()); // Toggle true/false
                    });
                    adapter.notifyItemChanged(position); // Actualizar color de esta fila
                }
            });
            if (selectedItems.isEmpty()) {
                tvEmptySummary.setVisibility(View.VISIBLE);
                recycler.setVisibility(View.GONE);
            } else {
                tvEmptySummary.setVisibility(View.GONE);
                recycler.setVisibility(View.VISIBLE);
            }
            recycler.setAdapter(adapter);

            // Calcular Totales
            double totalMoney = 0;
            int totalUnits = 0;
            int totalProducts = selectedItems.size();

            for (Item item : selectedItems) {
                totalMoney += (item.getPrice() * item.getQuantity());
                totalUnits += item.getQuantity();
            }

            tvTotal.setText(String.format("TOTAL: %.2f €", totalMoney));
            tvCountProducts.setText("Productos: " + totalProducts);
            tvCountUnits.setText("Unidades: " + totalUnits);

        } else {
            tvSummaryStore.setText("Ninguna tienda activa");
            tvTotal.setText("0.00 €");
            tvCountProducts.setText("-");
            tvCountUnits.setText("-");
            recycler.setAdapter(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.removeAllChangeListeners();
        if (realm != null) realm.close();
    }
}