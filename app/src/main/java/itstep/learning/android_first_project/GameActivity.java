package itstep.learning.android_first_project;
// new activity - empty activity

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private final int N = 4;
    private int[][] cells = new int[N][N];
    private final TextView[][] tvCells = new TextView[N][N];

    private final Random random = new Random();


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.game_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        LinearLayout gameField = findViewById(R.id.game_ll_field);

        gameField.post(() -> {
            //исполниться в очереди сообщений тогда  когда gameField будет готовый (для того , чтобы принимать посты и геты.)
            //действие  которые будут выполнены когда объект (gameField)  завершит свое построение , только в етот момент мы можем оращаться к ширине и высоте .
            //getWindiw - получаем физическое окно
            //getDecore - окно с учетом системных отступов
            int vw = this.getWindow().getDecorView().getWidth();

            int fieldMargin = 20;
            //изменяем параметры layout для етого gameField с целями сделать его квадратным.
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    vw - 2 * fieldMargin,
                    vw - 2 * fieldMargin
            );

            layoutParams.setMargins(fieldMargin, fieldMargin, fieldMargin, fieldMargin);
            layoutParams.gravity = Gravity.CENTER;
            gameField.setLayoutParams(layoutParams);

//
//            LinearLayout gameField = findViewById(R.id.game_ll_field);
//            gameField.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
//                int width = gameField.getWidth();
//                gameField.getLayoutParams().height = width;
//                gameField.requestLayout();
//            });

        });


        gameField.setOnTouchListener(new OnSwipeListener(GameActivity.this) {
            @Override
            public void onSwipeBottom() {
                Toast.makeText(GameActivity.this, "Bottom", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeLeft() {
                if(moveLeft()){
                    spawnCell();
                    showField();
                }
                else {
                    Toast.makeText(GameActivity.this, "No left move", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onSwipeRight() {
                Toast.makeText(GameActivity.this, "Right", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeTop() {
                Toast.makeText(GameActivity.this, "Top", Toast.LENGTH_SHORT).show();
            }
        });

        InitField();
        spawnCell();
        showField();
    }


    private boolean spawnCell(){
        //1 собрать данные про пустые клетки
        //2 выбрать одну случайно
        //3 поставить в неё 2 с вероятность 0,9 или 4 с вероятность 0,1
        //1 :
    List<Coordinates> freeCells = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if(cells[i][j] == 0){
                    freeCells.add(new Coordinates (i,j));
                }


            }}

        if(freeCells.isEmpty()){
            return false;
        }


        Coordinates randomCoordinates = freeCells.get(random.nextInt(freeCells.size()));
        cells[randomCoordinates.x][randomCoordinates.y] = random.nextInt(10) == 0 ? 4 : 2;

        return true;


    }


    private boolean moveLeft(){
        boolean result = false;
        //делаем алгоритм который перемещает все числа в левые позиции

        for(int i = 0 ; i < N ; i++) {
            int j0 = -1;
            for (int j = 0; j < N; j++) {
                if (cells[i][j] != 0) {
                    if (j0 == -1) {
                        j0 = j;
                    } else {
                        if (cells[i][j] == cells[i][j0]) {
                            //collapse

                            cells[i][j] *= 2;
                            cells[i][j0] = 0;
                            result = true;
                            j0 = -1;

                        }
                        else{
                            j0=j;
                        }
                    }
                }

            }

             j0 = -1;
            for(int j = 0 ; j < N ; j ++){
                if(cells[i][j] == 0){ //[ 0 2 0 4 ] -- [2 4 0 0 ]
                    if( j0 == -1){   //[0 0 0 2]    [0 0 2 2 ]
                        j0 = j;
                    }
                }
                else if(j0 != -1){
                    cells[i][j0] = cells[i][j];
                    cells[i][j] = 0;
                    j0 += 1;
                    result = true;
                }
            }
        }



        return result;
    }

            private void InitField () {
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
//                cells[i][j] = (int) Math.pow(2, i + j + 1);
                        cells[0][0] = 0;
                        tvCells[i][j] = findViewById(
                                getResources().getIdentifier(
                                        "game_cell_" + i + j,
                                        "id",
                                        getPackageName()
                                )
                        );
                    }
                }

            }

            private void showField() {
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        tvCells[i][j].setText(String.valueOf(cells[i][j]));
                        //тянем стилизации
                        tvCells[i][j].setBackgroundColor(getResources().getColor(
                                getResources().getIdentifier(
                                        cells[i][j] <= 2024 ? "game_tile_" + cells[i][j] : "game_tile_other",
                                        "color",
                                        getPackageName()  //ДЛЯ ТОГО , чтоб понял с какого пакета искать ресурс
                                ),
                                getTheme()
                        ));
                        tvCells[i][j].setTextColor(getResources().getColor(
                                getResources().getIdentifier(
                                        cells[i][j] <= 2024 ? "game_text_" + cells[i][j] : "game_text_other",
                                        "color",
                                        getPackageName()  //ДЛЯ ТОГО , чтоб понял с какого пакета искать ресурс
                                ),
                                getTheme()
                        ));

                    }
                }
            }

           static class Coordinates {
                int x, y;

                public Coordinates(int x, int y){
                    this.y = y;
                    this.x = x;
                }
            }


        }