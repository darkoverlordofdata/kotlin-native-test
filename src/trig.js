"use strict";
// Thanks to Riven
// From: http://riven8192.blogspot.com/2009/08/fastmath-sincos-lookup-tables.html
module.exports = class TrigLUT {
    static sin(rad) {
        return TrigLUT.sin_[(rad * TrigLUT.radToIndex) & TrigLUT.SIN_MASK];
    }
    static cos(rad) {
        return TrigLUT.cos_[(rad * TrigLUT.radToIndex) & TrigLUT.SIN_MASK];
    }
    static sinDeg(deg) {
        return TrigLUT.sin_[(deg * TrigLUT.degToIndex) & TrigLUT.SIN_MASK];
    }
    static cosDeg(deg) {
        return TrigLUT.cos_[(deg * TrigLUT.degToIndex) & TrigLUT.SIN_MASK];
    }
    static init(update) {
        TrigLUT.RAD = Math.PI / 180.0;
        TrigLUT.DEG = 180.0 / Math.PI;
        TrigLUT.SIN_BITS = 12;
        TrigLUT.SIN_MASK = ~(-1 << TrigLUT.SIN_BITS);
        TrigLUT.SIN_COUNT = TrigLUT.SIN_MASK + 1;
        TrigLUT.radFull = (Math.PI * 2.0);
        TrigLUT.degFull = (360.0);
        TrigLUT.radToIndex = TrigLUT.SIN_COUNT / TrigLUT.radFull;
        TrigLUT.degToIndex = TrigLUT.SIN_COUNT / TrigLUT.degFull;
        TrigLUT.sin_ = new Array(TrigLUT.SIN_COUNT);
        TrigLUT.cos_ = new Array(TrigLUT.SIN_COUNT);
        for (let i = 0; i < TrigLUT.SIN_COUNT; i++) {
            TrigLUT.sin_[i] = Math.sin((i + 0.5) / TrigLUT.SIN_COUNT * TrigLUT.radFull);
            TrigLUT.cos_[i] = Math.cos((i + 0.5) / TrigLUT.SIN_COUNT * TrigLUT.radFull);
        }
        if (update) {
            Math.sin = TrigLUT.sin;
            Math.cos = TrigLUT.cos;
        }
        return TrigLUT;
    }
}

