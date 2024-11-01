package itstep.learning.android_first_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //R - ресурсы , мы ищем ресурс просто по айди
        findViewById( R.id.main_btn_calc).setOnClickListener(this::onCalcButtonClick);

        findViewById( R.id.main_button_game).setOnClickListener(this::onGameButtonClick);
    }


    private void onCalcButtonClick(View view)
    {
        //intent - что-то типа new Task/ new Window  , запуск активности . Так м запускаем класс.
        Intent intent = new Intent(MainActivity.this, Calc_Activity.class);
        startActivity(intent);
    }

    private void onGameButtonClick(View view)
    {
        //intent - что-то типа new Task/ new Window  , запуск активности . Так м запускаем класс.
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        startActivity(intent);
    }

}