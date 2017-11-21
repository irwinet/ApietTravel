package application.android.irwinet.apiettravel.GridHome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import application.android.irwinet.apiettravel.R;

/**
 * Created by Irwinet on 19/11/2017.
 */

public class CustomAdapter  extends ArrayAdapter<Items> {
    ArrayList<Items> homeList=new ArrayList<>();

    public CustomAdapter (Context context,int texViewResourceId, ArrayList<Items> objects)
    {
        super(context,texViewResourceId,objects);
        homeList=objects;
    }

    @Override
    public int getCount()
    {
        return super.getCount();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v=convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.activity_grid_view_items, null);
        //TextView textView = (TextView) v.findViewById(R.id.textView);
        ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
        //textView.setText(homeList.get(position).getHomeListName());
        imageView.setImageResource(homeList.get(position).getHomeListImage());
        return v;
    }
}
