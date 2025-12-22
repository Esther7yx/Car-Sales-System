package com.carsale.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsale.entity.CarModel;
import com.carsale.service.CarModelService;
import com.carsale.utils.Result;
import com.carsale.utils.Result.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车型信息表Controller
 * 提供车型信息的RESTful API接口
 */
@RestController
@RequestMapping("/api/car-models")
public class CarModelController {

    @Autowired
    private CarModelService carModelService;

    /**
     * 分页查询车型信息，包含厂商关联
     * @param current 当前页码
     * @param size 每页大小
     * @param modelName 车型名称模糊查询
     * @param year 年份查询
     * @return 分页查询结果
     */
    @GetMapping("/page")
    public Result<List<CarModel>> pageQuery(@RequestParam(defaultValue = "1") long current,
                                            @RequestParam(defaultValue = "10") long size,
                                            @RequestParam(required = false) String modelName,
                                            @RequestParam(required = false) Integer year) {
        Page<CarModel> page = new Page<>(current, size);
        IPage<CarModel> pageResult = carModelService.selectPageWithManufacturer(page, modelName, year);
        
        PageInfo pageInfo = new PageInfo();
        pageInfo.setCurrent(pageResult.getCurrent());
        pageInfo.setSize(pageResult.getSize());
        pageInfo.setTotal(pageResult.getTotal());
        pageInfo.setPages(pageResult.getPages());
        pageInfo.setHasPrevious(pageResult.getCurrent() > 1);
        pageInfo.setHasNext(pageResult.getCurrent() < pageResult.getPages());
        
        return Result.success(pageResult.getRecords(), pageInfo);
    }

    /**
     * 根据ID查询车型信息
     * @param id 车型ID
     * @return 车型信息
     */
    @GetMapping("/{id}")
    public Result<CarModel> getById(@PathVariable Integer id) {
        CarModel carModel = carModelService.getById(id);
        if (carModel == null) {
            return Result.error("车型不存在");
        }
        return Result.success(carModel);
    }

    /**
     * 根据厂商ID查询车型列表
     * @param manufacturerId 厂商ID
     * @return 车型列表
     */
    @GetMapping("/by-manufacturer/{manufacturerId}")
    public Result<List<CarModel>> getByManufacturerId(@PathVariable Integer manufacturerId) {
        List<CarModel> carModels = carModelService.selectByManufacturerId(manufacturerId);
        return Result.success(carModels);
    }

    /**
     * 新增车型信息
     * @param carModel 车型信息
     * @return 新增结果
     */
    @PostMapping
    public Result<String> add(@RequestBody CarModel carModel) {
        // 验证厂商ID是否存在
        if (carModel.getManufacturerId() == null) {
            return Result.error("厂商ID不能为空");
        }
        
        // 验证车型名称是否已存在
        if (carModelService.checkModelNameExists(carModel.getManufacturerId(), carModel.getModelName(), null)) {
            return Result.error("该厂商下已存在相同名称的车型");
        }
        
        boolean saved = carModelService.save(carModel);
        if (saved) {
            return Result.success("新增车型成功");
        } else {
            return Result.error("新增车型失败");
        }
    }

    /**
     * 更新车型信息
     * @param id 车型ID
     * @param carModel 车型信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Integer id, @RequestBody CarModel carModel) {
        // 验证车型是否存在
        if (carModelService.getById(id) == null) {
            return Result.error("车型不存在");
        }
        
        // 设置车型ID
        carModel.setModelId(id);
        
        // 验证车型名称是否已存在
        if (carModelService.checkModelNameExists(carModel.getManufacturerId(), carModel.getModelName(), id)) {
            return Result.error("该厂商下已存在相同名称的车型");
        }
        
        boolean updated = carModelService.updateById(carModel);
        if (updated) {
            return Result.success("更新车型成功");
        } else {
            return Result.error("更新车型失败");
        }
    }

    /**
     * 删除车型信息
     * @param id 车型ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        // 验证车型是否存在
        if (carModelService.getById(id) == null) {
            return Result.error("车型不存在");
        }
        
        boolean deleted = carModelService.removeById(id);
        if (deleted) {
            return Result.success("删除车型成功");
        } else {
            return Result.error("删除车型失败");
        }
    }

    /**
     * 批量删除车型信息
     * @param ids 车型ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public Result<String> batchDelete(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的车型");
        }
        
        boolean deleted = carModelService.removeByIds(ids);
        if (deleted) {
            return Result.success("批量删除车型成功");
        } else {
            return Result.error("批量删除车型失败");
        }
    }
}