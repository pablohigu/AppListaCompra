package moviles.pablohiguero.examen2evlistacompra.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import moviles.pablohiguero.examen2evlistacompra.R;
import moviles.pablohiguero.examen2evlistacompra.adapters.ItemAdapter;
import moviles.pablohiguero.examen2evlistacompra.models.Item;
import moviles.pablohiguero.examen2evlistacompra.models.Store;

public class ShoppingListFragment extends Fragment {

    private RecyclerView recycler;
    private TextView tvActiveStore;
    private Realm realm;
    private ItemAdapter adapter;
    private Store activeStore;

    private final RealmChangeListener<Realm> realmListener = new RealmChangeListener<Realm>() {
        @Override
        public void onChange(Realm realm) {
            loadData();
        }
    };

    public ShoppingListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        realm = Realm.getDefaultInstance();
        // Registrar listener para refrescar automáticamente
        realm.addChangeListener(realmListener);

        recycler = view.findViewById(R.id.recyclerProducts);
        tvActiveStore = view.findViewById(R.id.tvActiveStoreName);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        loadData();

        return view;
    }

    private void loadData() {
        // Buscar tienda activa
        activeStore = realm.where(Store.class).equalTo("isActive", true).findFirst();

        if (activeStore != null) {
            tvActiveStore.setText("Tienda Activa: " + activeStore.getName());

            // Cargar items de esa tienda
            RealmResults<Item> items = activeStore.getItems().where().findAll();

            adapter = new ItemAdapter(items, new ItemAdapter.OnItemAction() {
                @Override
                public void onAddClick(Item item, int position) {
                    realm.executeTransaction(r -> item.setQuantity(item.getQuantity() + 1));
                    adapter.notifyItemChanged(position);
                }

                @Override
                public void onRemoveClick(Item item, int position) {
                    realm.executeTransaction(r -> {
                        if (item.getQuantity() > 0) {
                            item.setQuantity(item.getQuantity() - 1);

                            // Si baja a 0, deja de estar "comprado"
                            if (item.getQuantity() == 0) {
                                item.setPurchased(false);
                            }
                        }
                    });
                    adapter.notifyItemChanged(position);
                }

                @Override
                public void onItemClick(Item item, int position) {
                    // En la lista general no marco items, solo en el resumen
                }
            });
            recycler.setAdapter(adapter);
        } else {
            tvActiveStore.setText("Selecciona una tienda primero");
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