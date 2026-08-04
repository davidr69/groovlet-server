package net.lavacro.serverless.engine.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lavacro.serverless.annotations.GroovyService;
import net.openhft.hashing.LongTupleHashFunction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@GroovyService(value = "syslog")
public class SyslogService {
	private final JdbcTemplate jdbcTemplate;

	private final LongTupleHashFunction xxh128 = LongTupleHashFunction.xx128();

	public void log(Map<String, String> data) {
		String str = String.format("%s|%s|%d|%d",
				data.get("srcIp"), data.get("destIp"), data.get("destPort"), data.get("ipProtocol"));
		long[] hash = xxh128.hashBytes(str.getBytes(StandardCharsets.UTF_8));

		// Convert the 128-bit hash (two longs) into 16 bytes for MongoDB _id
		byte[] hashBytes = ByteBuffer.allocate(16)
				.order(ByteOrder.BIG_ENDIAN)
				.putLong(hash[0])
				.putLong(hash[1])
				.array();
	}
}
