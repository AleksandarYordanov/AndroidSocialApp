//package com.example.androidpostsapp.activities.adapters;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Filter;
//import android.widget.Filterable;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.androidpostsapp.R;
//import com.example.androidpostsapp.activities.SearchUserAdapterViewHolder;
//import com.example.androidpostsapp.models.Post;
//import com.example.androidpostsapp.models.User;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapterViewHolder> implements Filterable {
//
//    Context mCntx;
//    public ArrayList<User> arrayList;
//    public ArrayList<User> arrayListFiltered;
//
//    public SearchUserAdapter(Context mCntx, ArrayList<User> arrayList)
//    {
//        this.mCntx = mCntx;
//        this.arrayList = arrayList;
//        this.arrayListFiltered = new ArrayList<>(arrayList);
//    }
//
//    @Override
//    public int getItemCount()
//    {
//        return arrayList.size();
//    }
//
//    public long getItemId(int position)
//    {
//        return position;
//    }
//
//    @Override
//    public SearchUserAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
//    {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_search_layout, parent, false);
//
//        SearchUserAdapterViewHolder viewHolder = new SearchUserAdapterViewHolder(view);
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(SearchUserAdapterViewHolder holder, final int position)
//    {
//        final User user = arrayList.get(position);
//
//        holder.
//
//        holder..setText(arrayList.get(position).getBuilding());
//
//        Picasso.with(mCntx).load(user.getUserImg()).into(holder.image);//using picasso to load image
//
//        holder.cardUser.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(mCntx, CardviewSearch.class);
//                intent.putExtra("userId", String.valueOf(user.getUserId()));
//                intent.putExtra("building", String.valueOf(user.getBuilding()));
//                intent.putExtra("street", String.valueOf(user.getStreet()));
//                intent.putExtra("imgurl", String.valueOf(user.getUserImg()));
//                mCntx.startActivity(intent);
//
//            }
//        });
//    }
//
//
//
//    public Filter getFilter()
//    {
//        return exampleFilter;
//    }
//
//    private Filter exampleFilter = new Filter() {
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            List<User> filteredList = new ArrayList<>();
//
//            if (constraint == null || constraint.length() == 0) {
//                filteredList.addAll(arrayListFiltered);
//            } else {
//                String filterPattern = constraint.toString().toLowerCase().trim();
//
//                for (User item : arrayListFiltered) {
//                    if (item.getBuilding().toLowerCase().contains(filterPattern)) {
//                        filteredList.add(item);
//                    }
//                }
//            }
//
//            FilterResults results = new FilterResults();
//            results.values = filteredList;
//
//            return results;
//        }
//
//        @Override
//        protected void publishResults(CharSequence charSequence, FilterResults results) {
//            arrayList.clear();
//            arrayList.addAll((List)results.values);
//            notifyDataSetChanged();
//        }
//    };
//}
