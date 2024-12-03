package itstep.learning.android_first_project;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ChatActivity extends AppCompatActivity {

    private final String chatUrl = "https://chat.momentfor.fun";
    private LinearLayout chatContainer;

    private ScrollView chatScroller;
    private EditText etAuthor;
    private EditText etMessage;
    private View vBell;

    //GSON -
    private final Gson gson = new Gson();
    private final   ExecutorService threadPool = Executors.newFixedThreadPool(3);

    private final List<ChatMessage> messages = new ArrayList<>();


    //player
    private MediaPlayer incomingMessage;


    //smiles

    private final Map<String , String> emoji = new HashMap<>() {{
        // Smileys and Emotion
        put(":):", new String(Character.toChars(0x1F600))); // Grinning Face
        put(":D:", new String(Character.toChars(0x1F603))); // Smiling Face
        put(":;):", new String(Character.toChars(0x1F609))); // Winking Face
        put(":P", new String(Character.toChars(0x1F61B))); // Tongue Out
        put(":'(:", new String(Character.toChars(0x1F622))); // Crying Face
        put(":(:", new String(Character.toChars(0x1F641))); // Frowning Face

        // Animals
        put(":cat:", new String(Character.toChars(0x1F408))); // Cat
        put(":dog:", new String(Character.toChars(0x1F436))); // Dog
        put(":fox:", new String(Character.toChars(0x1F98A))); // Fox
        put(":panda:", new String(Character.toChars(0x1F43C))); // Panda

        // Objects
        put(":heart:", new String(Character.toChars(0x2764))); // Heart
        put(":star:", new String(Character.toChars(0x2B50))); // Star
        put(":fire:", new String(Character.toChars(0x1F525))); // Fire
        put(":phone:", new String(Character.toChars(0x1F4F1))); // Mobile Phone

        // Nature
        put(":sun:", new String(Character.toChars(0x2600))); // Sun
        put(":moon:", new String(Character.toChars(0x1F319))); // Crescent Moon
        put(":tree:", new String(Character.toChars(0x1F333))); // Deciduous Tree
        put(":flower:", new String(Character.toChars(0x1F33C))); // Blossom

        // Food
        put(":apple:", new String(Character.toChars(0x1F34E))); // Red Apple
        put(":pizza:", new String(Character.toChars(0x1F355))); // Pizza
        put(":coffee:", new String(Character.toChars(0x2615))); // Hot Beverage
        put(":cake:", new String(Character.toChars(0x1F382))); // Birthday Cake

        // Flags
        put(":flag_us:", new String(Character.toChars(0x1F1FA)) + new String(Character.toChars(0x1F1F8))); // US Flag
        put(":flag_fr:", new String(Character.toChars(0x1F1EB)) + new String(Character.toChars(0x1F1F7))); // France Flag
        put(":flag_jp:", new String(Character.toChars(0x1F1EF)) + new String(Character.toChars(0x1F1F5))); // Japan Flag

        // Symbols
        put(":check:", new String(Character.toChars(0x2714))); // Check Mark
        put(":cross:", new String(Character.toChars(0x274C))); // Cross Mark
        put(":warning:", new String(Character.toChars(0x26A0))); // Warning Sign

    }};
    //Для управление жизненным циклом в андроиде используется handler
    private final Handler handler = new Handler();

    private Animation bellAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });   ето растяжка нашей активности поо всему екрану

        chatContainer = findViewById(R.id.chat_ll_container);
        LinearLayout emojiContainer = findViewById(R.id.chat_ll_emoji);
        chatScroller = findViewById(R.id.chat_scroller);
        etAuthor = findViewById(R.id.chat_et_author);
        etMessage = findViewById(R.id.chat_et_message);
        vBell = findViewById(R.id.chat_bell);
        findViewById(R.id.chat_btn_send).setOnClickListener(this::sendButtonClick);
        bellAnimation = AnimationUtils.loadAnimation(this , R.anim.bell);
        incomingMessage = MediaPlayer.create(this , R.raw.jingle_lose);
        handler.post( this::periodic);
        //скроллер поднмиается при вызове клавиатуры
        chatScroller.addOnLayoutChangeListener((View v, int left, int top, int right,int bottom, int leftWas, int topWas, int rightWas, int bottomWas) -> chatScroller.post( ()-> chatScroller.fullScroll( View.FOCUS_DOWN )));



        for(Map.Entry<String, String> e : emoji.entrySet()){
            TextView tv = new TextView(this);
            tv.setText( e.getValue());
            tv.setTextSize(20);
            tv.setOnClickListener(v -> {
                etMessage.setText(etMessage.getText() + e.getValue());
                etMessage.setSelection(etMessage.getText().length());
            });
            emojiContainer.addView(tv);
        }

    urlToImageView("https://png.klev.club/uploads/posts/2024-05/png-klev-club-7m1i-p-patisson-png-23.png", findViewById(R.id.chat_img));
    }


    private void showNotification(){
        // Регистрируем канал в системе
        NotificationChannel channel = new NotificationChannel("ChatChannel" , "ChatChannel" , NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&  ActivityCompat.checkSelfPermission(this , android.Manifest.permission.POST_NOTIFICATIONS)!=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[] {android.Manifest.permission.POST_NOTIFICATIONS},
                    1002);
            return;
        }

        //отправка сообщения
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this , "ChatChannel")
                .setSmallIcon(android.R.drawable.star_big_on)
                .setContentTitle( "Чат")
                .setContentText( "Новое сообщение" )
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                ;

        Notification notification = builder.build();
        notificationManager.notify(1001 , notification);
    }



    private void urlToImageView(String url , ImageView imageView){
        CompletableFuture
                .supplyAsync( () ->{
                    try(InputStream inputStream = new URL(url).openStream()){

                        //Битмап - декодирует картинку
                        return BitmapFactory.decodeStream(inputStream);
                    }
                    catch (IOException e){
                        Log.e( "error in urlToImageView" , e.getMessage() == null ? e.getClass().toString() : e.getMessage());
                    return null;
                    }

                } , threadPool)
                .thenAccept( imageView::setImageBitmap);
        //либо thenAccept( bmp -> runOnUiThread( () -> imageView.setImageBitmap(bmp)));
    }

    private void periodic(){
        loadChat();
        handler.postDelayed ( this:: periodic , 3000);
    }


    private static final SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT);

    private void sendButtonClick(View view) {


        String author = etAuthor.getText().toString();
        if (author.isEmpty()) {
            Toast.makeText(this, "Empty failes :: Author", Toast.LENGTH_SHORT).show();
            return;
        }

        String message = etMessage.getText().toString();
        if (message.isEmpty()) {
            Toast.makeText(this, "Empty failes :: Message", Toast.LENGTH_SHORT).show();
            return;
        }
        CompletableFuture.runAsync(() ->
                sendChatMessage(new ChatMessage()
                        .setAuthor(author)
                        .setText(message)
                        .setMoment(sqlDateFormat.format(new Date()))
                ), threadPool);


    }

    public void sendChatMessage(ChatMessage chatMessage) {
        try {
            URL url = new URL(chatUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput( true ); // инпут - приход сообщения , ожидание ответа
            connection.setDoOutput(true); // наоборот - будет передавать данные ( тело)
            connection.setChunkedStreamingMode( 0 ); // отсылать одним пакетом ( не делить на чанки) , чанк - части пакета , на которы они делят


            //конфигурирация для отправки данные формы
            connection.setRequestMethod("POST");
            //заголовки
            connection.setRequestProperty("Content-Type" , "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept" , "application/json");
            connection.setRequestProperty("Connection" , "close");
            //тело
            OutputStream bodyStream = connection.getOutputStream();
            //формат сообщения формы : key1=value1&key2=value2   // ? = не нужно , иначе пойдет в url , а у нас форма

            bodyStream.write(
            String.format( "author=%s&msg=%s",
                    URLEncoder.encode(chatMessage.getAuthor()
                            , StandardCharsets.UTF_8.name()) ,
                    URLEncoder.encode(encodeEmoji(chatMessage.getText())
                            , StandardCharsets.UTF_8.name())
            ).getBytes(StandardCharsets.UTF_8));

            bodyStream.flush();   //передача запроса  В этот момент сообщение уходит .
            bodyStream.close();


            //ответ -
            int statusCode = connection.getResponseCode();
            if(statusCode  >= 200 && statusCode <300) { // OK
                Log.i("sendChatMessage" , "Message sent");
                loadChat();
            }
            else {  //ERRor
                InputStream responseStream = connection.getErrorStream();
                Log.e("sendChatMessage" , readString(responseStream));
                responseStream.close();
            }
            connection.disconnect();


        } catch (Exception ex) {
            Log.e("sendChatMessage", ex.getMessage() == null ? ex.getClass().toString() : ex.getMessage());
        }
    }

    private String encodeEmoji(String input) {
        for (Map.Entry<String, String> e : emoji.entrySet()) {
            input = input.replace(e.getValue(), e.getKey());
        }
        return input;
    }

    private String decodeEmoji(String input) {
        for (Map.Entry<String, String> e : emoji.entrySet()) {
            input = input.replace(e.getKey(), e.getValue());
        }
        return input;
    }
    private void loadChat() {
        CompletableFuture.supplyAsync(() ->
                        getChatAsString(), threadPool)
                .thenApply(this::processChatResponse)
                .thenAccept( m-> runOnUiThread( ()-> displayChatMessages(m)) );
    }


    private String getChatAsString() {
        try (InputStream urlStream = new URL(chatUrl).openStream()) {
            return readString(urlStream);


        } catch (MalformedURLException e) {
            Log.e("chatActivity::loadChat", e.getMessage() == null ? "MalformedURLException " : e.getMessage());
        } catch (IOException e) {
            Log.e("chatActivity::loadChat", e.getMessage() == null ? "IOException" : e.getMessage());
        }
        return null;
    }


    @SuppressLint("ResourceAsColor")
    private void displayChatMessages(ChatMessage[] chatMessages) {
        String nickName = etAuthor.getText().toString();
            // перевірити чи є нові повідомлення, якщо є, то додати до збереженої колекції,
            // якщо ні - ігнорувати виклик
            boolean wasNew = false;
            for( ChatMessage cm : chatMessages ) {
                if( messages.stream().noneMatch( m -> m.getId().equals( cm.getId() ) ) ) {
                    // нове повідомлення
                    cm.setText( decodeEmoji(cm.getText()));
                    messages.add( cm );
                    wasNew = true;
                }
            }
            if( ! wasNew ) return ;
            //сортируем по возрастании даты
        //messages.sort(Comparator.comparing(ChatMessage::getMoment));
//         // или
        messages.sort( (a,b) -> -b.getMoment().compareTo(a.getMoment()));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(10, 15, 50, 5);


        LinearLayout.LayoutParams layoutParams_wrapContent_other = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams_wrapContent_other.setMargins(50, 0, 15, 5);
        layoutParams_wrapContent_other.gravity = Gravity.RIGHT | Gravity.CENTER_HORIZONTAL;

            Drawable bgOther = getResources().getDrawable( R.drawable.chat_msg_other, getTheme() );
            Drawable bgAuthor = getResources().getDrawable(R.drawable.chat_message_my , getTheme());



            for( ChatMessage cm : messages ) {
                if(cm.getView() != null) continue;

                LinearLayout linearLayout = new LinearLayout( ChatActivity.this ) ;
                linearLayout.setOrientation( LinearLayout.VERTICAL );




                TextView tv = new TextView( ChatActivity.this );
                tv.setText( cm.getAuthor() + " " + cm.getMoment() );
                tv.setPadding( 30, 5, 30, 5 );
                linearLayout.addView( tv );

                tv = new TextView( ChatActivity.this );
                tv.setText( cm.getText() );
                tv.setPadding( 20, 5, 30, 5 );
                linearLayout.addView( tv );

                linearLayout.setBackground( bgOther );
                linearLayout.setLayoutParams( layoutParams );
                cm.setView(linearLayout);
                chatContainer.addView( linearLayout );


                if(nickName.equals(cm.getAuthor()))
                {
                    linearLayout.setBackground(bgAuthor);
                    linearLayout.setLayoutParams(layoutParams_wrapContent_other);
                }
            }




            //мы дожны скроллинг выкинуть в пост . чтобы она
        chatContainer.post( ()-> {
        chatScroller.fullScroll( View.FOCUS_DOWN);
        vBell.startAnimation(bellAnimation);
        incomingMessage.start();
            showNotification();
        });



    }

    private ChatMessage[] processChatResponse(String str) {

        ChatResponse chatResponse = gson.fromJson(str, ChatResponse.class);
        return chatResponse.data;
    }


    class ChatResponse {
        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public ChatMessage[] getData() {
            return data;
        }

        public void setData(ChatMessage[] data) {
            this.data = data;
        }

        private int status;
        private ChatMessage[] data;
    }

    /*
     * {"status" : 1,
     * "data" : [Chatmessage]
     * }
     * */

    class ChatMessage {

        private String id;
        private String author;
        private String text;
        private String moment;

        private View view;

        public View getView() {
            return view;
        }

        public ChatMessage setView(View view) {
            this.view = view;
            return this;
        }

        public String getId() {
            return id;
        }

        public ChatMessage setId(String id) {
            this.id = id;
            return this;
        }

        public String getAuthor() {
            return author;
        }

        public ChatMessage setAuthor(String author) {
            this.author = author;
            return this;
        }

        public String getText() {
            return text;
        }

        public ChatMessage setText(String text) {
            this.text = text;
            return this;
        }

        public String getMoment() {
            return moment;
        }

        public ChatMessage setMoment(String moment) {
            this.moment = moment;
            return this;
        }




    }


    /*читалка стринга со стрима*/
    private String readString(InputStream stream) throws IOException {
        //пользуемся байтбилжером вместо стрингбилдера , потому что кодировка мульбайтовая , и стрингбилдер может разрывать символы. Байтбилдер того не делает
        ByteArrayOutputStream byteBuilder = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int len;
        while ((len = stream.read(buffer)) != -1) {
            byteBuilder.write(buffer, 0, len);
        }

        String res = byteBuilder.toString(StandardCharsets.UTF_8.name());
        byteBuilder.close();
        return res;

    }


    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        threadPool.shutdownNow();
        super.onDestroy();
    }
}


/*
*
*
* Internet. Поулчение данных.
* Особенности : android.os.NetworkOnMainThreadException
    при попытке работать с сетями в основном/UI потоке происходит Exception :
    *
    * запускаем подклчение в новом потоке.
    *
    * java.lang.SecurityException: Permission denied (missing INTERNET permission?)
        для работы с Интернетом задекларируем разрешение ( в манифесте)
    *
    *
    *android.view.ViewRootImpl$CalledFromWrongThreadException:
    * Обращение до UI - setText , toasts , addView и  т.д. могут быть только из того потока в котормо они созданы .
       Для передачи работы к нему есть метод runOnUiThread(Runnable)
    *
    *
    *
 *
* */