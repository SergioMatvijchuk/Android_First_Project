package itstep.learning.android_first_project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class Calc_Activity extends AppCompatActivity {

private CalculatorModel calculator;
private TextView tv_result;

private static final int maxDigits = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });


        int[] numberIds = new int[]{
                R.id.calc_btn_digit_0,
                R.id.calc_btn_digit_1,
                R.id.calc_btn_digit_2,
                R.id.calc_btn_digit_3,
                R.id.calc_btn_digit_4,
                R.id.calc_btn_digit_5,
                R.id.calc_btn_digit_6,
                R.id.calc_btn_digit_7,
                R.id.calc_btn_digit_8,
                R.id.calc_btn_digit_9
        };

        int[] actionsIds = new int[]{
                R.id.calc_btn_operation_ac,
                R.id.calc_btn_operation_c,
                R.id.calc_btn_operation_backspace,
                R.id.calc_btn_operation_coma,
                R.id.calc_btn_operation_equal,
                R.id.calc_btn_operation_devide,
                R.id.calc_btn_operation_log,
                R.id.calc_btn_operation_minus,
                R.id.calc_btn_operation_multiply,
                R.id.calc_btn_operation_percent,
                R.id.calc_btn_operation_sqrt,
                R.id.calc_btn_operation_square,
                R.id.calc_btn_operation_pm,
                R.id.calc_btn_operation_plus
        };




        tv_result = findViewById(R.id.calc_tv_result);

        calculator = new CalculatorModel();

        View.OnClickListener numberButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculator.onNumPressed(((Button)v).getText().toString());
                tv_result.setText(calculator.getText());
            }
        };


        View.OnClickListener actionButtonOnClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                try {
                    calculator.onActionPressed(((Button)view).getText().toString());
                } catch (ParseException e) {
                    Toast.makeText(Calc_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                tv_result.setText(calculator.getText());
            }
        };


        for(int i = 0 ; i < numberIds.length ; i++){
            findViewById(numberIds[i]).setOnClickListener(numberButtonClickListener);
        }

        for(int i = 0 ; i < actionsIds.length ; i++){
            try{
                  findViewById(actionsIds[i]).setOnClickListener(actionButtonOnClickListener);
            }
            catch (Exception ignore){
                continue;
            }
        }

//try{
//    NumberFormat.getInstance(Locale.ROOT).parse("1.23").doubleValue();
//} catch (ParseException e) {
//    throw new RuntimeException(e);
//}


    }


}






