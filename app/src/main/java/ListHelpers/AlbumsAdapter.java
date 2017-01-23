package ListHelpers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import HelperClasses.UserConstants;
import us.tier5.digitalemployeeidadmin.digitalemployeeidadmin.LogActivity;
import us.tier5.digitalemployeeidadmin.digitalemployeeidadmin.R;
import us.tier5.digitalemployeeidadmin.digitalemployeeidadmin.ShowBadge;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder> {

    private Context mContext;
    private List<Album> albumList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count, action,tvEmployeeName,tvBeaconName;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            action = (TextView) view.findViewById(R.id.action);
            tvEmployeeName = (TextView) view.findViewById(R.id.tvEmployeeName);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            tvBeaconName = (TextView) view.findViewById(R.id.tvBeaconName);


            //overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }


    public AlbumsAdapter(Context mContext, List<Album> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Album album = albumList.get(position);
        holder.title.setText(album.getEmploeeId());
        holder.count.setText(album.getTime());
        if(album.getAction().equals(""))
        {
            holder.action.setVisibility(View.GONE);
        }
        else
        {
            holder.action.setText(album.getAction());
        }

        holder.tvEmployeeName.setText(album.getName());

        // loading album cover using Glide library
        Glide.with(mContext).load(album.getImageUrl()).placeholder(R.drawable.photo_place_holder).error(R.drawable.broken_image).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UserConstants.currentEmployeeId = album.getEmploeeId();

                showPopupMenu(holder.overflow);
            }
        });

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, ShowBadge.class);
                UserConstants.currentIdToShow=album.getIdcard();
                mContext.startActivity(intent);
            }
        });

        if(album.getBeaconName().equals(""))
        {
            holder.tvBeaconName.setVisibility(View.GONE);
        }
        else
        {
            holder.tvBeaconName.setText(album.getBeaconName());
        }
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.show_all_logs:
                    //Toast.makeText(mContext, "All Logs", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(mContext, ""+UserConstants.currentEmployeeId, Toast.LENGTH_SHORT).show();
                    UserConstants.userSpecificLog=true;
                    Intent intent1 = new Intent(mContext, LogActivity.class);
                    mContext.startActivity(intent1);
                    return true;
                case R.id.beacon_specific_logs:
                    //Toast.makeText(mContext, "Beacon Specific Logs", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(mContext, ""+UserConstants.currentEmployeeId, Toast.LENGTH_SHORT).show();
                    UserConstants.userBeaconSpecificLog=true;
                    Intent intent2 = new Intent(mContext, LogActivity.class);
                    mContext.startActivity(intent2);
                    return true;
                default:
            }
            return false;
        }
    }



    @Override
    public int getItemCount() {
        return albumList.size();
    }
}
