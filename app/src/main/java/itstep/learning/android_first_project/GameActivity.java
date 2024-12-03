package itstep.learning.android_first_project;
// new activity - empty activity

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private final String bestScore_filename = "best_score.2048";

    private final int N = 4;
    private int[][] cells = new int[N][N];
    private int[][] undo;
    private int score , bestScore;
    private int prevScore; // for undo

    private final TextView[][] tvCells = new TextView[N][N];

    private final Random random = new Random();

    private Animation spawnAnimation , collapseAnimation , bellAnimation;
    private final String spawnTag = "spawn";
    private final String collapseTag = "collapse";



    private TextView tvScore , tvBestScore;





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


        spawnAnimation = AnimationUtils.loadAnimation(this , R.anim.game_spawn);
        collapseAnimation = AnimationUtils.loadAnimation(this, R.anim.game_collapse);
        bellAnimation = AnimationUtils.loadAnimation(this, R.anim.bell);

        tvScore = findViewById(R.id.game_tv_score);
        tvBestScore = findViewById(R.id.game_tv_best_score);
        findViewById(R.id.game_btn_undo).setOnClickListener( v -> undoMove());

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

        });


        gameField.setOnTouchListener(new OnSwipeListener(GameActivity.this) {
            @Override
            public void onSwipeBottom() {
                Toast.makeText(GameActivity.this, "Bottom", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeLeft() {
                if ( canMoveLeft() ) {
                    saveField();
                    moveLeft();
                    spawnCell();
                    showField();
                } else {
                    Toast.makeText(GameActivity.this, "No left move", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onSwipeRight() {
                if (moveRight()) {
                    spawnCell();
                    showField();
                } else {
                    Toast.makeText(GameActivity.this, "No right move", Toast.LENGTH_SHORT).show();
                }

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


    private void saveBestScore(){
        try(
                FileOutputStream fos = openFileOutput(bestScore_filename , Context.MODE_PRIVATE);
                DataOutputStream writer = new DataOutputStream( fos );
        ) {
            writer.writeInt(bestScore);     //пишет в файл данные инт
            writer.flush(); // сохраняет файл на диск
        }
        catch (IOException e) {
            Log.e("GameActivity::saveBestScore" , e.getMessage() != null ? e.getMessage() : "Error writing file");
        }

    }


    private void loadBestScore(){
        try(FileInputStream fis = openFileInput(bestScore_filename )) {
            DataInputStream reader = new DataInputStream( fis );
            bestScore = reader.readInt();     //пишет в файл данные инт

        }
        catch (IOException e) {
            Log.e("GameActivity::loadBestScore" , e.getMessage() != null ? e.getMessage() : "Error reading file");
        }

    }

    private boolean canMoveLeft(){

        for (int i = 0; i < N; i++) {
            for(int j = 1 ; j < N ; j++){
                if( cells[i][j] != 0 && ( cells[i][j-1] == 0 || cells[i][j-1] == cells[i][j])) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean spawnCell() {
        //1 собрать данные про пустые клетки
        //2 выбрать одну случайно
        //3 поставить в неё 2 с вероятность 0,9 или 4 с вероятность 0,1
        //1 :
        List<Coordinates> freeCells = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (cells[i][j] == 0) {
                    freeCells.add(new Coordinates(i, j));
                }
            }
        }

        if (freeCells.isEmpty()) {
            return false;
        }


        Coordinates randomCoordinates = freeCells.get(random.nextInt(freeCells.size()));
        cells[randomCoordinates.x][randomCoordinates.y] = random.nextInt(10) == 0 ? 4 : 2;

        tvCells[randomCoordinates.x][randomCoordinates.y].setTag(spawnAnimation);

        return true;


    }


    private void moveLeft() {

        //делаем алгоритм который перемещает все числа в левые позиции

        for (int i = 0; i < N; i++) {
            int j0 = -1;
            for (int j = 0; j < N; j++) {
                if (cells[i][j] != 0) {
                    if (j0 == -1) {
                        j0 = j;
                    } else {
                        if (cells[i][j] == cells[i][j0]) {
                            //collapse
                            score += cells[i][j];
                            cells[i][j] *= 2;
                            tvCells[i][j].setTag(collapseTag);  //помечаем нужну клеточку
                            cells[i][j0] = 0;
                            j0 = -1;

                        } else {
                            j0 = j;
                        }
                    }
                }

            }

            j0 = -1;
            for (int j = 0; j < N; j++) {
                if (cells[i][j] == 0) { //[ 0 2 0 4 ] -- [2 4 0 0 ]
                    if (j0 == -1) {   //[0 0 0 2]    [0 0 2 2 ]
                        j0 = j;
                    }
                } else if (j0 != -1) {
                    // здесь происходит перемещение
                    cells[i][j0] = cells[i][j];

                    tvCells[i][j0].setTag(tvCells[i][j].getTag());
                    cells[i][j] = 0;
                    tvCells[i][j].setTag(null);
                    j0 += 1;

                }
            }
        }
   }





  private void saveField(){
        prevScore = score;
        undo = new int[N][N];
        for(int i = 0 ; i < N ; i++){
                 System.arraycopy(cells[i], 0, undo[i] , 0 , N);
            }


  }


  private void showUndoMessage(){
        new AlertDialog
                .Builder(this , androidx.appcompat.R.style.ThemeOverlay_AppCompat_Dark)
                .setTitle("Ограничение")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Отмена ход невозможна")
                .setNeutralButton("Закрыть" , (dlg, btn) -> {})
                .setPositiveButton("Подписка" , (dlg, btn) -> {} )
                .setNegativeButton("Выйти" , (dlg, btn) -> finish() )
                .setCancelable( false )
                .show();
  }
  private void undoMove(){
        if(undo == null){
            showUndoMessage();
            return;
        }
        score = prevScore;
       for(int j = 0 ; j < N ; j++){
          System.arraycopy( undo[j], 0, cells[j], 0 , N);
      }
        undo = null;
      showField();

  }


    private boolean moveRight() {
       boolean result = false;
        for (int i = 0; i < N; i++) {
            boolean wasShift;
            do {
                wasShift = false;
                for (int j = N - 1; j > 0; j--) {
                    if (cells[i][j] == 0 && cells[i][j - 1] != 0) {
                        cells[i][j] = cells[i][j - 1];
                        cells[i][j - 1] = 0;
                        wasShift = result = true;
                    }
                }
            } while (wasShift);
            //collapse

            for (int j = N - 1; j > 0; j--) {
                if (cells[i][j - 1] == cells[i][j] && cells[i][j]!= 0) {
                   cells[i][j] *=2;
                   tvCells[i][j].setTag(collapseAnimation);
                   score += cells[i][j];
                  // cells[i][j-1] = 0;

                   for(int k = j - 1  ; k > 0  ; k--){
                       cells[i][k] = cells[i][k-1];
                   }
                   cells[i][0] = 0;     //[ 0 2 2 8]

                }
            }
        }
        return result;
    }

    private void InitField() {
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

        score = 0;
        loadBestScore();



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


                if( tvCells[i][j].getTag() instanceof Animation){

                    tvCells[i][j].startAnimation((Animation) tvCells[i][j].getTag());
                    tvCells[i][j].setTag( null );
                }

            }
        }
        tvScore.setText(getString(R.string.game_tv_score , String.valueOf(score)));
        if(bestScore < score){
            bestScore = score;
            saveBestScore();

            tvBestScore.startAnimation(bellAnimation);
        }
        tvBestScore.setText(getString(R.string.game_tv_best , String.valueOf(bestScore)));
    }


    static class Coordinates {
        int x, y;

        public Coordinates(int x, int y) {
            this.y = y;
            this.x = x;
        }
    }


/*
/data/data/itstep.learning.android_first_project/files/best_score.2048
* */

}