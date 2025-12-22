package com.carsale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsale.entity.Vehicle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 车辆信息表Mapper接口
 * 继承MyBatis Plus的BaseMapper，提供基础的CRUD操作
 */
public interface VehicleMapper extends BaseMapper<Vehicle> {
    /**
     * 根据车型ID查询车辆列表
     * @param modelId 车型ID
     * @return 车辆列表
     */
    List<Vehicle> selectByModelId(@Param("modelId") Integer modelId);

    /**
     * 根据车辆状态查询车辆列表
     * @param status 车辆状态
     * @return 车辆列表
     */
    List<Vehicle> selectByStatus(@Param("status") String status);

    /**
     * 分页查询车辆信息，包含车型和厂商关联
     * @param page 分页参数
     * @param vin VIN码模糊查询
     * @param status 车辆状态查询
     * @param modelId 车型ID查询
     * @return 分页结果
     */
    IPage<Vehicle> selectPageWithDetails(Page<Vehicle> page, 
                                         @Param("vin") String vin, 
                                         @Param("status") String status, 
                                         @Param("modelId") Integer modelId);

    /**
     * 查询当前库存车辆（在库状态）
     * @return 库存车辆列表
     */
    List<Vehicle> selectCurrentInventory();

    /**
     * 根据VIN码更新车辆状态
     * @param vin VIN码
     * @param status 新状态
     * @return 更新数量
     */
    int updateStatusByVin(@Param("vin") String vin, @Param("status") String status);
}
