package fpoly.huynqph26074.executerservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import fpoly.huynqph26074.executerservice.DataAdapter;


public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DataAdapter dataAdapter;
    private List<Document> listdocuments;
    Document deletedDocument;
    ExecutorService executorService;
    private EditText ed_name, ed_price, ed_brand;
    Button btn_them, btn_sua;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        executorService = Executors.newCachedThreadPool();
        ed_name = findViewById(R.id.ed_name);
        ed_price = findViewById(R.id.ed_price);
        ed_brand = findViewById(R.id.ed_brand);
        btn_them = findViewById(R.id.btn_them);
        btn_sua = findViewById(R.id.btn_sua);


        recyclerView = findViewById(R.id.id_recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listdocuments = new ArrayList<>();
        dataAdapter = new DataAdapter(listdocuments);
        recyclerView.setAdapter(dataAdapter);


        dataAdapter.setOnItemClickListener(new DataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Document document = listdocuments.get(position);
                ed_name.setText(document.getName());
                ed_price.setText(document.getPrice());
                ed_brand.setText(document.getBrand());



                btn_sua.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String tens = ed_name.getText().toString();
                        String gias = ed_price.getText().toString();
                        String hangs = ed_brand.getText().toString();

                        Document documentSua = new Document(tens, gias, hangs);


                        updateData("http://192.168.137.47:3000/product/:id", documentSua.getId(), tens, gias, hangs, new ResponseListener() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("ExecuterService", response);
                            }

                            @Override
                            public void onError(Exception e) {
                                // Xử lý lỗi nếu có
                            }
                        });
                    }
                });
            }
        });
        btn_them.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ten = ed_name.getText().toString();
                String gia = ed_price.getText().toString();
                String hang = ed_brand.getText().toString();

                Document document = new Document(ten, gia, hang);
                listdocuments.add(document);
                dataAdapter.notifyDataSetChanged();


                postData("http://192.168.137.47:3000/product/post", ten, gia, hang, new ResponseListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ExecuterService", response);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
                try {
                    Future<JSONArray> future = executorService.submit(new MyCallable());
                    if (future.get() != null) {
                        Log.d("ExecuterService", future.get().toString());
                    }
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });




        getDanhSach();

        // executorService.shutdown();
    }

    private void getDanhSach() {
        // Sử dụng ExecutorService hoặc các phương thức khác để lấy dữ liệu từ máy chủ
        // Sau khi nhận được dữ liệu, cập nhật productList và thông báo cho Adapter
        try {
            Future<JSONArray> future = executorService.submit(new MyCallable());
            JSONArray jsonArray = future.get();
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("name");
                    String price = jsonObject.getString("price");
                    String brand = jsonObject.getString("brand");
                    listdocuments.add(new Document(name, price, brand));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dataAdapter.notifyDataSetChanged();
                    }
                });
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }
/// delete
// Trong một phương thức của MainActivity


    private JSONArray callAPIGetData(String urlString) throws IOException {
        // Tạo URL từ đường dẫn đã cho
        URL url = new URL(urlString);
        // Mở kết nối
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            // Đọc dữ liệu từ kết nối
            InputStream in = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            // Chuyển dữ liệu đọc được thành một JSONArray
            return new JSONArray(result.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } finally {
            // Đóng kết nối
            urlConnection.disconnect();
        }
    }
////


    public void updateData(final String urlString, int documen, String name, String price, String brand, ResponseListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(urlString);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("PUT");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    // Tạo đối tượng JSON từ dữ liệu
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", name);
                    jsonObject.put("price", price);
                    jsonObject.put("brand", brand);

                    // Gửi dữ liệu
                    DataOutputStream outStream = new DataOutputStream(connection.getOutputStream());
                    outStream.writeBytes(jsonObject.toString());
                    outStream.flush();
                    outStream.close();

                    // Đọc phản hồi từ server
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Gửi phản hồi cho listener
                    if (listener != null) {
                        listener.onResponse(response.toString());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    // Gửi lỗi cho listener
                    if (listener != null) {
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        });
    }

    public interface ResponseListener {
        void onResponse(String response);

        void onError(Exception e);
    }

    public void postData(final String urlString, final String name, final String price, final String brand, final ResponseListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(urlString);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    // Tạo đối tượng JSON từ dữ liệu
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", name);
                    jsonObject.put("price", price);
                    jsonObject.put("brand", brand);

                    // Gửi dữ liệu
                    DataOutputStream outStream = new DataOutputStream(connection.getOutputStream());
                    outStream.writeBytes(jsonObject.toString());
                    outStream.flush();
                    outStream.close();

                    // Đọc phản hồi từ server
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Gửi phản hồi cho listener
                    if (listener != null) {
                        listener.onResponse(response.toString());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    // Gửi lỗi cho listener
                    if (listener != null) {
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        });
    }

    //    public interface ResponseListener {
//        void onResponse(String response);
//        void onError(Exception e);
//    }
    public class MyRunable implements Runnable {
        String name;

        public MyRunable(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            try {
                Log.d("ExecuterService",name+" dang chạy");
                Thread.sleep(200);
                Log.d("ExecuterService",name+" chết");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public class MyCallable implements Callable<JSONArray>{

        @Override
        public JSONArray call() throws Exception {
            JSONArray jsonArray = callAPIGetData("http://192.168.137.47:3000/product/getall");
            return jsonArray;
        }
    }


}