#!/bin/bash
# redis服务器所在IP地址
HOST=127.0.0.1
# 测试结果文件后缀，用于区分不同的测试情形
# 常见设置：
# 100M：表示100M网络状况
# loopback: 本地回环测试
# tcpip: 通过tcpip网络
POSTFIX="loopback-100M"
#POSTFIX="tcpip-1000M"
# 当吞吐率小于此数值时中止本轮测试
TPS_THRESHOULD=5000
# 待测试的redis命令
CMDS="set"
# 同时测试的线程数，默认50
CLIENTS=50
# 起始测试数据尺寸
FIRST=2
# 结束测试数据尺寸。要结合总测试次数确定结束测试数据尺寸，防止超出内存分配
# 通常8G内存可以设置为30000
FINAL=30000
# 步长增长因子，每次增长10%
FACTOR=10
# 测试数据序列，默认为空
SEQUENCE=""
# 键空间
KEYSPACE=100000
# 总测试次数
TOTAL_REQUESTS=1000000
# 测试结果文件
FILE_BENCHMARK=redis-benchmark
CURRENT_DATE=`date +%Y%m%d`
# 产生测试数据序列
data_size=$FIRST
while [ $data_size -le $FINAL ]
do
    SEQUENCE=${SEQUENCE}" "${data_size};
    step=$[$data_size / $FACTOR];
    if [ $step -le 10 ]; then
        step=10
    fi
    data_size=$[$data_size + $step];
done
 
echo $SEQUENCE
 
tps=0

function benchmark(){
    data_size=$1
    pipeline=$2
    bench_file=$3
    redis-cli  flushall > /dev/null
    if [ x"$pipeline" != "x" ];then
        with_pipeline="-P $pipeline"
    fi
    benchmark_cmd="redis-benchmark -h $HOST -c $CLIENTS  $with_pipeline -r $KEYSPACE -n $TOTAL_REQUESTS -t $CMDS -d $data_size"
    echo $benchmark_cmd
    tps=`$benchmark_cmd | grep "requests per second" | awk '{print $1}'`
    echo "$data_size,$tps" | tee -a $bench_file
}
 
# 不使用pipeline情形
# 首先清空benchmark数据文件
bench_file=${FILE_BENCHMARK}-${POSTFIX}-${CURRENT_DATE}.dat
cp /dev/null $bench_file
for data_size in $SEQUENCE; do
    benchmark $data_size "" $bench_file
    echo $tps
    if [ ${tps%%.*} -le $TPS_THRESHOULD ]; then
        break;
    fi
done
 
# 开启pipeline情形
# 这里只测试8..64时的情形
# TODO 耗时太长，似乎应该减少测试的轮次数
for pipeline in {8,16,32,64}; do
    bench_file=${FILE_BENCHMARK}-${POSTFIX}-P${pipeline}-${CURRENT_DATE}.dat
    cp /dev/null $bench_file
    for data_size in $SEQUENCE; do
        benchmark $data_size $pipeline $bench_file
        if [ ${tps%%.*} -le $TPS_THRESHOULD ]; then
            break;
        fi
    done
done
