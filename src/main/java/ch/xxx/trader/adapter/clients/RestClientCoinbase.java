/**
 *    Copyright 2016 Sven Loesekann

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ch.xxx.trader.adapter.clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.xxx.trader.domain.dtos.QuoteCb;
import ch.xxx.trader.domain.dtos.WrapperCb;
import reactor.core.publisher.Mono;

public class RestClientCoinbase {
	private static final String URL = "https://api.coinbase.com/v2";
	
	public static void main(String[] args) {
//		RestClientCoinbase client = new RestClientCoinbase();
//		client.testIt();
		WebClient wc = WebClient.create(URL);		
		QuoteCb quoteCb = wc.get().uri("/exchange-rates?currency=BTC")
                .accept(MediaType.APPLICATION_JSON_UTF8).exchange()
                .flatMap(response ->	response.bodyToMono(WrapperCb.class))
                .flatMap(resp -> Mono.just(resp.getData()))
                .flatMap(resp2 -> Mono.just(resp2.getRates()))
                .block();		
		System.out.println(quoteCb.toString());
		
	}		
	
	   private void testIt(){

		      String https_url = "https://api.coinbase.com/v2/exchange-rates?currency=BTC";
		      URL url;
		      try {

			     url = new URL(https_url);
			     HttpsURLConnection con = (HttpsURLConnection)url.openConnection();			     

			     //dump all the content
			     print_content(con);

		      } catch (MalformedURLException e) {
			     e.printStackTrace();
		      } catch (IOException e) {
			     e.printStackTrace();
		      }

		   }
	   
	   private void print_content(HttpsURLConnection con){
		   ObjectMapper mapper = new ObjectMapper();
		   if(con!=null){

			try {

			   System.out.println("****** Content of the URL ********");
			   BufferedReader br =
				new BufferedReader(
					new InputStreamReader(con.getInputStream()));

			   String input;

			   while ((input = br.readLine()) != null){
			      if(!input.matches("Content")) {
			    	  WrapperCb w = mapper.readValue(input, WrapperCb.class);
			    	  System.out.println(w.getData().getRates().getUsd());
			      }
				   System.out.println(input);
			   }
			   br.close();

			} catch (IOException e) {
			   e.printStackTrace();
			}

		       }

		   }
}
