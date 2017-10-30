package com.hfut.service.impl;

import com.hfut.entity.RemoteExpWorkload;
import com.hfut.entity.RemoteExpWorkloadExample;
import com.hfut.mapper.RemoteExpWorkloadMapper;
import com.hfut.service.RemoteExpService;
import com.hfut.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RemoteExpServiceImpl implements RemoteExpService {
    @Autowired
    private RemoteExpWorkloadMapper workloadMapper;

    @Override
    public List<RemoteExpWorkload> findAllLoad(Integer offset, Integer limit) throws Exception {
        RemoteExpWorkloadExample example = new RemoteExpWorkloadExample();
        RemoteExpWorkloadExample.Criteria criteria = example.createCriteria();
        criteria.andIdIsNotNull();
        example.setOffset(offset);
        example.setLimit(limit);
        return workloadMapper.selectByExample(example);
    }

    @Override
    public int getCount() throws Exception {
        RemoteExpWorkloadExample example = new RemoteExpWorkloadExample();
        RemoteExpWorkloadExample.Criteria criteria = example.createCriteria();
        criteria.andIdIsNotNull();
        return workloadMapper.countByExample(example);
    }

    @Override
    public boolean updateLoad(RemoteExpWorkload workload) throws Exception {
        RemoteExpWorkloadExample workloadExample = new RemoteExpWorkloadExample();
        //计算工作量 课时
        int people = workload.getPeople();
        if (people<=27) {
            workload.setTypeCoefficient(0.6F);
        }else if (people>=28&&people<37) {
            workload.setTypeCoefficient(0.8F);
        }else if (people>=37&&people<=55) {
            workload.setTypeCoefficient(1.0F);
        }

        workload.setWorkload(workload.getPeriod() * workload.getReCoefficient() * workload.getTypeCoefficient());
        workload.setMoney(PropertyUtil.getAllowance() * workload.getWorkload());

        RemoteExpWorkloadExample.Criteria criteria = workloadExample.createCriteria();
        criteria.andIdEqualTo(workload.getId());
        if (workloadMapper.updateByExample(workload, workloadExample) == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean removeLoad(Integer id) throws Exception {
        RemoteExpWorkloadExample expWorkloadExample = new RemoteExpWorkloadExample();
        RemoteExpWorkloadExample.Criteria criteria = expWorkloadExample.createCriteria();
        criteria.andIdEqualTo(id);
        if (workloadMapper.deleteByExample(expWorkloadExample) == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean insertLoad(RemoteExpWorkload workload) throws Exception {
        //计算工作量 课时
        workload.setWorkload(workload.getTypeCoefficient() * workload.getReCoefficient() * workload.getPeriod());
        //计算酬金
        workload.setMoney(PropertyUtil.getAllowance() * workload.getWorkload());
        if (workloadMapper.insertSelective(workload) == 0) {
            return false;
        }
        return true;
    }

    @Override
    public List<RemoteExpWorkload> insertLoadByList(List<RemoteExpWorkload> list) throws Exception {
        ArrayList<RemoteExpWorkload> ret = new ArrayList<RemoteExpWorkload>();
        for (RemoteExpWorkload workload : list) {
            if (!insertLoad(workload)) {
                ret.add(workload);
            }
        }
        return ret;
    }
}