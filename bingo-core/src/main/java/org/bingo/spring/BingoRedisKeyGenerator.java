package org.bingo.spring;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;

public class BingoRedisKeyGenerator implements KeyGenerator {

	@Override
	public Object generate(Object target, Method method, Object... params) {
		StringBuilder sb = new StringBuilder();
		sb.append(target.getClass().getName());
		sb.append(method.getName());
		for (Object obj : params) {
			sb.append(obj.toString());
		}
		return sb.toString();
	}

}
