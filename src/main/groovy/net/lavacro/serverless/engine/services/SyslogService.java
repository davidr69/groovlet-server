package net.lavacro.serverless.engine.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lavacro.serverless.annotations.GroovyService;
import net.openhft.hashing.LongTupleHashFunction;
import org.intellij.lang.annotations.Language;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@GroovyService(value = "syslog")
public class SyslogService {
	private final JdbcTemplate jdbcTemplate;

	private final LongTupleHashFunction xxh128 = LongTupleHashFunction.xx128();

	@Language("SQL")
	private static final String INSERT_HASH = """
		INSERT INTO syslog_sources (id, hostname, source_ip, protocol, dest_port, fqdn, domain)
		VALUES (?::uuid, ?, ?::inet, ?, ?, ?, ?)
		ON CONFLICT DO NOTHING
	""";

	@Language("SQL")
	private static final String INSERT_EVENT = """
		INSERT INTO log_events (id, event_timestamp)
		VALUES (?::uuid, ?)
	""";

	public void log(Map<String, String> data) {
		log.info("Write data: {}", data);
		String str = String.format("%s|%s|%s|%s",
				data.get("hostname"), data.get("address"), data.get("port"), data.get("protocol"));

		log.info("string: {}", str);

		long[] hash = xxh128.hashBytes(str.getBytes(StandardCharsets.UTF_8));

		// Convert the 128-bit hash (two longs) into 16 bytes for MongoDB _id
		byte[] hashBytes = ByteBuffer.allocate(16)
				.order(ByteOrder.BIG_ENDIAN)
				.putLong(hash[0])
				.putLong(hash[1])
				.array();

		log.info("Hash bytes: {}", hashBytes);

		String uuid = String.format("%02x%02x%02x%02x-%02x%02x-%02x%02x-%02x%02x-%02x%02x%02x%02x%02x%02x",
				hashBytes[0], hashBytes[1], hashBytes[2], hashBytes[3],
				hashBytes[4], hashBytes[5], hashBytes[6], hashBytes[7],
				hashBytes[8], hashBytes[9], hashBytes[10], hashBytes[11],
				hashBytes[12], hashBytes[13], hashBytes[14], hashBytes[15]);

		log.info("UUID: {}", uuid);

		/*
		 * data:
		 *
		 * {
		 * 	"hostname":"server",
		 * 	"date":"2025-03-28T22:26:15.638929-04:00",
		 *  "address":"148.113.206.49",
		 *  "protocol":"TCP",
		 * 	"port":36984,
		 *  "fqdn": "...",
		 *  "domain": "..."
		 * }
		 */
		int count = jdbcTemplate.update(
				INSERT_HASH,
				uuid,
				data.get("hostname"),
				data.get("address"),
				data.get("protocol"),
				data.get("port"),
				data.get("fqdn"),
				data.get("domain")
		);

		log.info("upsert result: {}", count);

		OffsetDateTime dt = OffsetDateTime.parse(data.get("date"));

		count = jdbcTemplate.update(
				INSERT_EVENT,
				uuid,
				dt
		);
		log.info("event result: {}", count);
	}
}
