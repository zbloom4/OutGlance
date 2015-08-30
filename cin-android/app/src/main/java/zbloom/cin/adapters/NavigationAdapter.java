package zbloom.cin.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import zbloom.cin.models.Navigation;
import zbloom.cin.R;

/**
 * Created by bloom on 4/26/15.
 */
public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.MyViewHolder>{

    private LayoutInflater inflater;
    List<Navigation> navigation = Collections.emptyList();


    public NavigationAdapter(Context context, List<Navigation> navigation){
        inflater = LayoutInflater.from(context);
        this.navigation = navigation;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row, parent, false);

        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Navigation current = navigation.get(position);
        holder.title.setText(current.getTitle());
        holder.icon.setImageResource(current.getIconID());
    }

    @Override
    public int getItemCount() {
        return navigation.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title;
        ImageView icon;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.listText);
            icon = (ImageView) itemView.findViewById(R.id.listIcon);
            icon.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {


        }


    }
}
