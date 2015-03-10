package madfox.colhh.madfoxcustom.httpclients;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public final class HttpClientMadfox {

	private boolean get_method=true;
	private int response_code=-1;
	private InputStream in;
	private ArrayList<NameValuePair> params=null;
	
	/*Getters & Setters*/
	/**
	 * set HTTP request Method
	 * @param isGet
	 */
	public final void setMethod(boolean isGet)
	{
		this.get_method=isGet;
	}
	/**
	 * Get Response Code
	 * @return
	 */
	public final int getResponseCode()
	{
		return response_code;
	}
	/**
	 * Set Values for POST
	 * @param params
	 */
	public final void setParamsForPost(ArrayList<NameValuePair> params)
	{
		this.params=params;
	}
	/**
	 * Connect using Background Thread
	 * @param url
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public final void connectAndGet(String url) throws IOException, InterruptedException
	{
		new AsyncTask<String, Void, Void>() {
			@Override
			protected Void doInBackground(String... params) {
				try {
					getSmallData(params[0]);
				} catch (IOException e) {
					Log.e("connectAndGet", e.getMessage());
					return null;
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) 
			{
				if(ORL!=null) ORL.onReceive(HttpClientMadfox.this);
			};
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url);
	}
	/**
	 * Connect and get an InputStream
	 * @param url
	 * @return an InputStream
	 * @throws IOException
	 */
	private final Void getSmallData(String surl) throws IOException
	{
		InputStream is = null;
		try {
			URL url = new URL(surl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(10000);
			conn.setRequestMethod(get_method ? "GET" : "POST");
			conn.setDoOutput(!get_method);
			conn.setDoInput(true);
			if(!get_method)
			{
				conn = setPostParams(params,conn);
				if(conn==null){ return null;}
			}
			conn.connect();
			
			response_code = conn.getResponseCode();
			is=conn.getInputStream();
		}finally
		{
			this.in=is;
		}
		return null;
	}
	/**
	 * write POST params
	 * @param params
	 * @param conn
	 * @return whether success 
	 */
	private HttpURLConnection setPostParams(ArrayList<NameValuePair> params,HttpURLConnection conn)
	{
		HttpURLConnection con=conn;
		try {
			OutputStream out = con.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
			writer.write(getPOSTQuery(params));
			writer.flush();
			writer.close();
			out.close();
		} catch (IOException e) {
			Log.e("setPostParams", e.getMessage());
			return null;
		}
		return con;
	}
	/**
	 * Encode Params
	 * @param params
	 * @return Encoded String
	 * @throws UnsupportedEncodingException
	 */
	private String getPOSTQuery(ArrayList<NameValuePair> params) throws UnsupportedEncodingException
	{
		StringBuilder result = new StringBuilder();
		boolean first = true;
		
		for(NameValuePair pair : params)
		{
			if(first)
			{
				first=false;
			}else
			{
				result.append("&");
			}
			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(pair.getValue(),"UTF-8"));
		}
		
		return result.toString();
	}
	/**
	 * Read As String
	 * @param in
	 * @param len
	 * @return
	 * @throws IOException
	 */
	public final String readAsString(int len) throws IOException,NullPointerException
	{
		String stream=null;
		try{
			Reader reader = null;
			reader = new InputStreamReader(in,"UTF-8");
			char[] buffer = new char[len];
			reader.read(buffer);
			stream = new String(buffer);
		}finally
		{
			in.close();
		}
		return stream;
	}
	/**
	 * Get network connectivity status
	 * @param context
	 * @return whether network is connected
	 */
	public static final boolean isConnected(Context context)
	{
		ConnectivityManager conmngr = (ConnectivityManager) 
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = conmngr.getActiveNetworkInfo();
		if(netinfo != null && netinfo.isConnected()) return true;
		return false;
	}
	
	/**
	 * Interface
	 */
	OnReceiveListener ORL;
	public interface OnReceiveListener
	{
		public void onReceive(HttpClientMadfox client);
	}
	
	public void setOnReceiveListener(OnReceiveListener ORL)
	{
		this.ORL=ORL;
	}
}
