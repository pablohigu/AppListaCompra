package moviles.pablohiguero.examen2evlistacompra;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import moviles.pablohiguero.examen2evlistacompra.fragments.ShoppingListFragment;
import moviles.pablohiguero.examen2evlistacompra.fragments.StoresFragment;
import moviles.pablohiguero.examen2evlistacompra.fragments.SummaryFragment;


public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        bottomNav = findViewById(R.id.bottomNavigation);

        // CONFIGURACIÓN DEL ADAPTADOR
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // 1. EVENTO CLICK EN BARRA INFERIOR -> CAMBIA PAGINA
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_stores) {
                    viewPager.setCurrentItem(0);
                    return true;
                } else if (itemId == R.id.nav_list) {
                    viewPager.setCurrentItem(1);
                    return true;
                } else if (itemId == R.id.nav_summary) {
                    viewPager.setCurrentItem(2);
                    return true;
                }
                return false;
            }
        });

        // 2. EVENTO SWIPE (DESLIZAR) -> CAMBIA SELECCIÓN BARRA
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNav.setSelectedItemId(R.id.nav_stores);
                        break;
                    case 1:
                        bottomNav.setSelectedItemId(R.id.nav_list);
                        break;
                    case 2:
                        bottomNav.setSelectedItemId(R.id.nav_summary);
                        break;
                }
            }
        });
    }

    // CLASE INTERNA: ADAPTADOR DE FRAGMENTS
    class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new StoresFragment();
                case 1: return new ShoppingListFragment();
                case 2: return new SummaryFragment();
                default: return new StoresFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}