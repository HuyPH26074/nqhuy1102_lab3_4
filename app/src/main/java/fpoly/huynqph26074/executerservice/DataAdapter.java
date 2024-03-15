package fpoly.huynqph26074.executerservice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private List<Document> dataList;
    private Context context;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Phương thức này để thiết lập trình nghe sự kiện

    // ViewHolder và các phương thức khác của Adapter


    public DataAdapter(Context context) {
        this.context = context;
    }

    public DataAdapter(List<Document> dataList) {
        this.dataList = dataList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Document document = dataList.get(position);
        holder.tv_name.setText(document.getName());
        holder.tv_price.setText(document.getPrice());
        holder.tv_brand.setText(document.getBrand());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });

        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (listener != null) {
//                    listener.onDeleteClick(position);
//                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setData(List<Document> dataList) {
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_price, tv_brand;
        ImageView  img_delete;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_brand = itemView.findViewById(R.id.tv_brand);
            tv_price = itemView.findViewById(R.id.tv_price);
            img_delete = itemView.findViewById(R.id.img_delete);

        }
    }
}
