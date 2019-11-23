package com.roc.chatclient.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.roc.chatclient.R;
import com.roc.chatclient.entity.User;
import com.roc.chatclient.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class EaseContactAdapter extends ArrayAdapter<User> implements SectionIndexer {
    private static final String TAG = "ContactAdapter";
    List<String> list;
    List<User> userList;
    List<User> copyUserList;
    private LayoutInflater layoutInflater;
    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;
    private int res;
    private MyFilter myFilter;
    private boolean notiyfyByFilter;

    public EaseContactAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        this.res = resource;
        this.userList = objects;
        copyUserList = new ArrayList<User>();
        copyUserList.addAll(objects);
        layoutInflater = LayoutInflater.from(context);
    }

    private static class ViewHolder {
        ImageView avatar;
        TextView nameView;
        TextView headerView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            if (res == 0)
                convertView = layoutInflater.inflate(R.layout.ease_row_contact, null);
            else
                convertView = layoutInflater.inflate(res, null);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            holder.nameView = (TextView) convertView.findViewById(R.id.name);
            holder.headerView = (TextView) convertView.findViewById(R.id.header);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User user = getItem(position);
        if (user == null)
            Log.d("ContactAdapter", position + "");
        String username = user.Name;
        String header = user.getInitialLetter();

        if (position == 0 || header != null && !header.equals(getItem(position - 1).getInitialLetter())) {
            if (TextUtils.isEmpty(header)) {
                holder.headerView.setVisibility(View.GONE);
            } else {
                holder.headerView.setVisibility(View.VISIBLE);
                holder.headerView.setText(header);
            }
        } else {
            holder.headerView.setVisibility(View.GONE);
        }

        setUserNick(user, holder.nameView);
//        EaseUserUtils.setUserNick(username, holder.nameView);
//        EaseUserUtils.setUserAvatar(getContext(), username, holder.avatar);


        if (primaryColor != 0)
            holder.nameView.setTextColor(primaryColor);
        if (primarySize != 0)
            holder.nameView.setTextSize(TypedValue.COMPLEX_UNIT_PX, primarySize);
        if (initialLetterBg != null)
            holder.headerView.setBackgroundDrawable(initialLetterBg);
        if (initialLetterColor != 0)
            holder.headerView.setTextColor(initialLetterColor);

        return convertView;
    }

    @Override
    public User getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public int getPositionForSection(int section) {
        return positionOfSection.get(section);
    }

    @Override
    public int getSectionForPosition(int position) {
        return sectionOfPosition.get(position);
    }

    @Override
    public Object[] getSections() {
        positionOfSection = new SparseIntArray();
        sectionOfPosition = new SparseIntArray();
        int count = getCount();
        list = new ArrayList<String>();
        list.add(getContext().getString(R.string.search_header));
        positionOfSection.put(0, 0);
        sectionOfPosition.put(0, 0);
        for (int i = 1; i < count; i++) {

            String letter = getItem(i).getInitialLetter();
            int section = list.size() - 1;
            if (list.get(section) != null && !list.get(section).equals(letter)) {
                list.add(letter);
                section++;
                positionOfSection.put(section, i);
            }
            sectionOfPosition.put(i, section);
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    public Filter getFilter() {
        if (myFilter == null) {
            myFilter = new MyFilter(userList);
        }
        return myFilter;
    }

    protected class MyFilter extends Filter {
        List<User> mOriginalList = null;

        public MyFilter(List<User> myList) {
            this.mOriginalList = myList;
        }

        @Override
        protected synchronized FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (mOriginalList == null) {
                mOriginalList = new ArrayList<User>();
            }
            Log.d(TAG, "contacts original size: " + mOriginalList.size());
            Log.d(TAG, "contacts copy size: " + copyUserList.size());

            if (prefix == null || prefix.length() == 0) {
                results.values = copyUserList;
                results.count = copyUserList.size();
            } else {
                String prefixString = prefix.toString();
                final int count = mOriginalList.size();
                final ArrayList<User> newValues = new ArrayList<User>();
                for (int i = 0; i < count; i++) {
                    final User user = mOriginalList.get(i);
                    String username = user.Name;

                    if (username.startsWith(prefixString)) {
                        newValues.add(user);
                    } else {
                        final String[] words = username.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(user);
                                break;
                            }
                        }
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }
            Log.d(TAG, "contacts filter results size: " + results.count);
            return results;
        }

        @Override
        protected synchronized void publishResults(CharSequence constraint,
                                                   FilterResults results) {
            userList.clear();
            userList.addAll((List<User>) results.values);
            Log.d(TAG, "publish contacts filter results size: " + results.count);
            if (results.count > 0) {
                notiyfyByFilter = true;
                notifyDataSetChanged();
                notiyfyByFilter = false;
            } else {
                notifyDataSetInvalidated();
            }
        }
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (!notiyfyByFilter) {
            copyUserList.clear();
            copyUserList.addAll(userList);
        }
    }

    protected int primaryColor;
    protected int primarySize;
    protected Drawable initialLetterBg;
    protected int initialLetterColor;

    public EaseContactAdapter setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
        return this;
    }


    public EaseContactAdapter setPrimarySize(int primarySize) {
        this.primarySize = primarySize;
        return this;
    }

    public EaseContactAdapter setInitialLetterBg(Drawable initialLetterBg) {
        this.initialLetterBg = initialLetterBg;
        return this;
    }

    public EaseContactAdapter setInitialLetterColor(int initialLetterColor) {
        this.initialLetterColor = initialLetterColor;
        return this;
    }

    private void setUserNick(User user, TextView textView) {
        if (textView != null) {
            if (user != null && !StringUtils.isEmpty(user.NickName)) {
                textView.setText(user.NickName);
            } else {
                textView.setText(user.Name);
            }
        }
    }

//    private void setUserAvatar(Context context, User user, ImageView imageView){
//
//        if(user != null && !StringUtils.isEmpty(user.Avatar)){
//            try {
//                int avatarResId = Integer.parseInt(user.Avatar);
//                Glide.with(context).load(avatarResId).into(imageView);
//            } catch (Exception e) {
//                Glide.with(context)
//                        .load(user.Avatar)
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .placeholder(R.drawable.ease_default_avatar)
//                        .error(R.drawable.ease_default_avatar)
//                        .bitmapTransform(new CropCircleTransformation(context))
//                        .into(imageView);
//
//            }
//        }else{
//            Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);
//        }
//    }
}
