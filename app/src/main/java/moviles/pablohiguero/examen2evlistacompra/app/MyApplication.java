package moviles.pablohiguero.examen2evlistacompra.app;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import moviles.pablohiguero.examen2evlistacompra.models.Store;
import moviles.pablohiguero.examen2evlistacompra.utils.Utils;


public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        setUpRealmConfig();

        Realm realm = Realm.getDefaultInstance();

        // REGLA DEL EXAMEN: Si la BBDD está vacía, cargamos los datos de prueba
        // Comprobamos si hay alguna Store guardada
        if (realm.where(Store.class).count() == 0) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm r) {
                    // Utils.getSampleData() nos devuelve la lista de tiendas con sus productos
                    r.copyToRealm(Utils.getSampleData());
                }
            });
        }

        realm.close();
    }

    private void setUpRealmConfig() {
        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration.Builder()
                .allowWritesOnUiThread(true) // Permite escribir en el hilo principal
                .deleteRealmIfMigrationNeeded() // Evita crasheos si cambia algo en Store/Item
                .build();
        Realm.setDefaultConfiguration(config);
    }
}