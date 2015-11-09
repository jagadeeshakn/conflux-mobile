package com.mifos.mifosxdroid.adapters;

/**
 * Created by conflux37 on 10/22/2015.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.MenuList;
import com.mifos.objects.MenuListData;
import com.squareup.picasso.Picasso;

/**
 * Created by megha on 15-03-06.
 */
public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.ViewHolder> {

    Context mContext;
    OnItemClickListener mItemClickListener;
    FrameLayout frameLayout;


    public MenuListAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_row_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MenuList place = new MenuListData().getMenuList().get(position);


        holder.menuName.setText(place.name);
        Picasso.with(mContext).load(place.getImageResourceId(mContext)).fit().into(holder.menuImage);
        //Picasso.with(mContext).load(place.getImageResourceId(mContext)).resize(75,75).into(holder.menuImage);
        Bitmap photo = BitmapFactory.decodeResource(mContext.getResources(), place.getImageResourceId(mContext));

        Palette.generateAsync(photo, new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                int mutedLight = palette.getMutedColor(mContext.getResources().getColor(android.R.color.black));
                holder.menuNameHolder.setBackgroundColor(mutedLight);
            }
        });
    }

    @Override
    public int getItemCount() {
        return new MenuListData().getMenuList().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public LinearLayout menuHolder;
        public LinearLayout menuNameHolder;
        public TextView menuName;
        public ImageView menuImage;

        public ViewHolder(View itemView) {
            super(itemView);
            menuHolder = (LinearLayout) itemView.findViewById(R.id.mainHolder);
            menuName = (TextView) itemView.findViewById(R.id.placeName);
            menuNameHolder = (LinearLayout) itemView.findViewById(R.id.placeNameHolder);
            menuImage = (ImageView) itemView.findViewById(R.id.placeImage);
            menuHolder.setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

}