package com.ecolearn.backend.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    private static final long OTP_VALID_DURATION = 2 * 60 * 1000; // 2 minutes in ms
    private final Map<String, OtpData> otpCache = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public String generateOtp(String email) {
        String otp = String.format("%06d", random.nextInt(1000000));
        long expiryTime = System.currentTimeMillis() + OTP_VALID_DURATION;
        otpCache.put(email, new OtpData(otp, expiryTime));
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        OtpData data = otpCache.get(email);
        if (data == null) {
            return false;
        }
        if (data.expiryTime < System.currentTimeMillis()) {
            otpCache.remove(email);
            return false;
        }
        if (data.otp.equals(otp)) {
            otpCache.remove(email);
            return true;
        }
        return false;
    }

    private static class OtpData {
        String otp;
        long expiryTime;

        OtpData(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }
}
