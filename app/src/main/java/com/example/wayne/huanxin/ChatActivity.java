package com.example.wayne.huanxin;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMContactManager;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.adapter.message.EMATextMessageBody;

import java.util.List;
import java.util.Objects;

public class ChatActivity extends Activity {
    private Button sendButton;
    private EditText inputContentEditText;
    private ListView chatListView;
    private EMConversation conversation;
    private String toChatUserName="jianyun";
    private DataAdapter adapter;

    EMMessageListener msgListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendButton=(Button)findViewById(R.id.send);
        inputContentEditText=(EditText)findViewById(R.id.input_content);
        chatListView=(ListView)findViewById(R.id.chat_listview);


        conversation= EMClient.getInstance().chatManager().
                getConversation(toChatUserName, EMConversation.EMConversationType.Chat, true);
        if(conversation!=null) {
            adapter = new DataAdapter();
            chatListView.setAdapter(adapter);
            chatListView.setSelection(chatListView.getCount() - 1);
        }

        msgListener=new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> list) {

                for(EMMessage message:list){
                    String username=message.getFrom();
                    if(username.equals(toChatUserName)){
                        conversation.getAllMessages().add(message);

                    }else{
                        return;
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageReadAckReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageChanged(EMMessage emMessage, Object o) {

            }
        };
        EMClient.getInstance().chatManager().addMessageListener(msgListener);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EMMessage message = EMMessage.createTxtSendMessage(inputContentEditText.getText().toString(), toChatUserName);
                conversation.getAllMessages().add(message);
                inputContentEditText.setText("");
                //发送消息
                EMClient.getInstance().chatManager().sendMessage(message);

                adapter.notifyDataSetChanged();
            }
        });

    }

    private class DataAdapter extends BaseAdapter{
        TextView textViewName;
        @Override
        public int getCount(){
            return conversation.getAllMessages().size();
        }
        @Override
        public Object getItem(int position){
            return conversation.getAllMessages().get(position);
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position,View convertView,ViewGroup parent){
            EMMessage message=conversation.getAllMessages().get(position);
            EMTextMessageBody body=(EMTextMessageBody)message.getBody();
            if(message.direct()==EMMessage.Direct.RECEIVE){
                if(message.getType()== EMMessage.Type.TXT){
                    convertView= LayoutInflater.from(ChatActivity.this).inflate(R.layout.listview_item,null);
                    textViewName=(TextView)convertView.findViewById(R.id.textname);
                    textViewName.setText(message.getFrom());
                }
            }else{
                if(message.getType()== EMMessage.Type.TXT){
                    convertView= LayoutInflater.from(ChatActivity.this).inflate(R.layout.listview_item1,null);
                }
            }
            TextView textViewContent=(TextView)convertView.findViewById(R.id.textcontent);
            textViewContent.setText(body.getMessage());

            return convertView;
        }
    }
}
