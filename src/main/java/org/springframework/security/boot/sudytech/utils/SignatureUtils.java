/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.security.boot.sudytech.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 
 * @author ： <a href="https://github.com/hiwepy">wandl</a>
 */
public class SignatureUtils {

	public static String getSignature(String securyKey, String timestamp, String userId, String nonce, String uxid)
			throws NoSuchAlgorithmException {

		List<String> strs = new ArrayList<String>();
		strs.add(securyKey);
		strs.add(timestamp);
		strs.add(userId);
		strs.add(nonce);
		strs.add(uxid);
		
		Collections.sort(strs, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		
		StringBuffer sb = new StringBuffer();
		for (String str : strs) {
			sb.append(str);
		}
		
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(sb.toString().getBytes());
		
		return toHex(md.digest());
	}

	public static String toHex(byte[] buffer) {
		StringBuilder sb = new StringBuilder(buffer.length * 2);
		for (int i = 0; i < buffer.length; i++) {
			sb.append(Character.forDigit((buffer[i] & 240) >> 4, 16));
			sb.append(Character.forDigit(buffer[i] & 15, 16));
		}
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		String securyKey = "123456";
		String timestamp = "1546072289881";
		String uId = "123";
		String nonce = "123";
		String uxid = "123";
		System.out.println(getSignature(securyKey, timestamp, uId, nonce, uxid));
	}

}
