# 《费马大定理》书中的有趣算法

## 完美数 / Perfect Number
### 完美数定义
一个正整数的所有因子求和，根据求和值将数分为下面几类：
- 所有因子的和等于此数，这个数是完美数
- 所有因子的和大与此数，这个数是盈数
- 所有因子的和小于此数，这个数是亏数

### 功能
1. 判定一个给定的数是否是完美数、还是亏数、或盈数，并打印出所有因子。
2. 输出第N个完美数，打印出所有因数，并打印出完美数的欧拉表达式。

### 判定一个给定的数是否是完美数
#### 算法描述
1. 计算给定数的因素，一次计算N个（如1000个）
2. 如果因数小于N个，则表示没有更多因数，计算结束
3. 如果因数大于N个，则表示还有更多的因素，则对N个数按照步骤1,2继续计算因素

#### 实现逻辑
1. 2个DMS队列，一个存放待计算的数（CaculatorQuene)，一个存放计算后的数(ResultQueue)
2. 首次计算时候，将给定的数放入CaculatorQueue
3. DMS触发求因素的计算函数f1
4. 函数f1按照算法求因素，将中间结果放入CaculatorQueue，将最终结果放入ResultQueue
5. DMS触发求结果的计算函数f2，f2更新RequestQuque

### 查找第N个完美数