package com.harry.core.util;


import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;


/**
 * @author Tony Luo
 */
public final class BloomFilterUtil {

  private BloomFilterUtil() {
  }

  private static final Logger log = LoggerFactory.getLogger(BloomFilterUtil.class);

  public static <T> BloomFilter createBloomFilter(Funnel funnel, Integer expectedInsertions,
      Double falseProbability, Collection<T> insertions) {
    BeanUtil.checkNotNull(funnel, "Funnel must not be null");
    BeanUtil.checkNotNull(expectedInsertions, "expectedInsertions must not be null");
    BeanUtil.checkNotNull(falseProbability, "falseProbability must not be null");

    BeanUtil.checkArgument(expectedInsertions >= 0,
        String.format("Expected insertions (%s) must be >= 0", expectedInsertions));
    BeanUtil
        .checkArgument(falseProbability > 0.0, String.format("False probability (%s) must be > 0.0", falseProbability));
    BeanUtil
        .checkArgument(falseProbability < 1.0, String.format("False probability (%s) must be < 1.0", falseProbability));

    BloomFilter<T> bloomFilter = BloomFilter.create(funnel, expectedInsertions, falseProbability);

    if (!CollectionUtils.isEmpty(insertions)) {
      for (T insert : insertions) {
        bloomFilter.put(insert);
      }
    }

    return bloomFilter;
  }

  public static <T> BloomFilter put(BloomFilter<T> bloomFilter, Collection<T> insertionList) {
    if (CollectionUtils.isEmpty(insertionList)) {
      return bloomFilter;
    }

    for (T insertion : insertionList) {
      put(bloomFilter, insertion);
    }
    return bloomFilter;
  }

  public static <T> BloomFilter put(BloomFilter<T> bloomFilter, T insertion) {
    BeanUtil.checkNotNull(bloomFilter, "BloomFilter must not be null");
    BeanUtil.checkNotNull(insertion, "insertion must not be null");

    if (insertion == null) {
      return bloomFilter;
    }

    bloomFilter.put(insertion);

    return bloomFilter;
  }

  public static <T> byte[] generateBloomFilterBytes( Funnel funnel, Integer expectedInsertions,
      Double falseProbability,Collection<T> insertions) {

    if (CollectionUtils.isEmpty(insertions)) {
      return null;
    }

    BloomFilter<T> bloomFilter = createBloomFilter(funnel, expectedInsertions, falseProbability, insertions);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      bloomFilter.writeTo(baos);

    } catch (IOException e) {
      log.error("[generateBloomFilterBytes] error", e);
    }
    return baos.toByteArray();
  }

  public static <T> byte[] convertBloomFilter2Bytes(BloomFilter<T> bloomFilter) {

    BeanUtil.checkNotNull(bloomFilter, "BloomFilter must not be null");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      bloomFilter.writeTo(baos);

    } catch (IOException e) {
      log.error("[convertBloomFilter2Bytes] error", e);
    }
    return baos.toByteArray();
  }


  public static <T> BloomFilter buildBloomFilter(byte[] bytes, Funnel funnel) {
    if (bytes == null) {
      return null;
    }

    BloomFilter<T> bloomFilter = null;
    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

    try {
      bloomFilter = BloomFilter.readFrom(bais, funnel);
    } catch (IOException e) {
      log.error("[buildBloomFilter] error", e);

    }
    return bloomFilter;
  }

}
