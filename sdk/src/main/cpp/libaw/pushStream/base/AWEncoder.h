//
// Created by Administrator on 2018/3/8.
//

#ifndef NATIVE_LIBRARY_AWENCODER_H
#define NATIVE_LIBRARY_AWENCODER_H

/**
 * 前向定义，使编译通过
 */
class AWEncoderManager;
/**
 * 基类
 */
class AWEncoder {
public:
    AWEncoderManager *manager;
public:
    AWEncoder(){};
    virtual ~AWEncoder(){};
    /**
     * 开始
     */
    virtual void open() = 0;
    /**
     * 结束
     */
    virtual void close() = 0;
};

#endif //NATIVE_LIBRARY_AWENCODER_H
