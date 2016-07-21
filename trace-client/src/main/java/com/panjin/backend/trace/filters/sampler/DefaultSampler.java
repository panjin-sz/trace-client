/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.filters.sampler;

import java.util.Random;

/**
 *
 *
 * @author panjin
 * @version $Id: DefaultSampler.java 2016年7月21日 下午5:39:01 $
 */
public class DefaultSampler implements Sampler {
    
    public static final String DEFAULT_SAMPLE_RATE = "0.7";                                            // 默认采样率

    private float              rate                = Float.valueOf(DefaultSampler.DEFAULT_SAMPLE_RATE); // 默认的采样率

    public DefaultSampler(float rate) {
        this.rate = rate;
    }

    public DefaultSampler() {
    }

    /** 
     * @see com.panjin.backend.trace.filters.sampler.Sampler#isSample()
     */
    @Override
    public boolean isSample() {
        Random random = new Random();
        // 默认70%采用率
        return random.nextFloat() <= rate;
    }

}
