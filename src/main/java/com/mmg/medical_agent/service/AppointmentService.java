package com.mmg.medical_agent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mmg.medical_agent.entity.Appointment;

public interface AppointmentService extends IService<Appointment> {
    Appointment getOne(Appointment appointment);
}

