package andreew.vlak;

import java.util.*;
import android.content.Context;
import android.media.Image;
import android.media.ImageWriter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Adapter extends ArrayAdapter<PolozkaScore> {

    Context context;
    int layoutResourceId;   
    List<PolozkaScore> data = null;

    HashMap<String, Integer> hmap = new HashMap<String, Integer>();
   
    public Adapter(Context context, int layoutResourceId, List<PolozkaScore> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;

        /*
        hmap.put("AUD", R.drawable.flag_aud);
        hmap.put("BGN", R.drawable.flag_bgn);
        hmap.put("BRL", R.drawable.flag_brl);
        hmap.put("CAD", R.drawable.flag_cad);
        hmap.put("CHF", R.drawable.flag_chf);
        hmap.put("CNY", R.drawable.flag_cny);
        hmap.put("CZ", R.drawable.flag_cz);
        hmap.put("DKK", R.drawable.flag_dkk);
        */
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        EntryHolder holder = null;
        this.context = context;
        if(row == null) {
        	LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );      	
            row = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new EntryHolder();
            /*
            holder.txtKod = (TextView)row.findViewById(R.id.txtKod);
            holder.txtMena = (TextView)row.findViewById(R.id.txtMena);
            holder.txtZeme = (TextView)row.findViewById(R.id.txtZeme);
            holder.txtCena = (TextView)row.findViewById(R.id.txtCena);
            holder.imgFlag = (ImageView) row.findViewById(R.id.imgFlag);
*/
            row.setTag(holder);
        }
        else {
            holder = (EntryHolder)row.getTag();
        }
       /*
        Entry entry = data.get(position);
        holder.txtKod.setText(entry.kod);
        holder.txtMena.setText(entry.mena);
        holder.txtZeme.setText(entry.zeme);
        holder.txtCena.setText(entry.cena);
        */
        /*
        holder.imgFlag.setImageResource(
                context.getResources().getIdentifier("flag_"+entry.kod.toLowerCase(), "drawable", context.getPackageName())

        );
        */

        return row;
    }
   
    static class EntryHolder {
        TextView txtKod;
        TextView txtMena;
        TextView txtZeme;
        TextView txtCena;
        ImageView imgFlag;
    }


}
