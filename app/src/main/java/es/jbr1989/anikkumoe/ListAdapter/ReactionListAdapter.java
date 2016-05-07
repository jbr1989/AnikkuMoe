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

/**
 * Created by jbr1989 on 21/04/2016.
 */
public class ReactionListAdapter extends ArrayAdapter<ReactionActivity.Country> {

    private final List<ReactionActivity.Country> list;
    private final Activity context;

    static class ViewHolder {
        protected TextView name;
        protected ImageView flag;
    }

    public ReactionListAdapter(Activity context, List<ReactionActivity.Country> list) {
        super(context, R.layout.reaction, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.reaction, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.flag = (ImageView) view.findViewById(R.id.flag);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(list.get(position).getName());
        holder.flag.setImageDrawable(list.get(position).getFlag());
        return view;
    }


}
