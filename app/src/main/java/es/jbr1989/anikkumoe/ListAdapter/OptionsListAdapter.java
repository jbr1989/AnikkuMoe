package es.jbr1989.anikkumoe.ListAdapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.ReactionActivity;
import es.jbr1989.anikkumoe.activity.imgActivity;

/**
 * Created by jbr1989 on 27/09/2017.
 */

public class OptionsListAdapter extends ArrayAdapter<imgActivity.ImgOpcion> {

    private final List<imgActivity.ImgOpcion> list;
    private final Activity context;

    static class ViewHolder {
        protected TextView name;
        protected ImageView flag;
    }

    public OptionsListAdapter(Activity context, List<imgActivity.ImgOpcion> list) {
        super(context, R.layout.item_option, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.reaction, null);
            final ReactionListAdapter.ViewHolder viewHolder = new ReactionListAdapter.ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }

        ReactionListAdapter.ViewHolder holder = (ReactionListAdapter.ViewHolder) view.getTag();
        holder.name.setText(list.get(position).getName());
        return view;
    }
}
