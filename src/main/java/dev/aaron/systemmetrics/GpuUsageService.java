package dev.aaron.systemmetrics;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class GpuUsageService {
    private static final String GPU_USAGE_COMMAND = "nvidia-smi --query-gpu=utilization.gpu --format=csv,noheader,nounits";

    public double getGpuUsage() {
        String gpuUsage = "Unknown";
        try {
            Process process = Runtime.getRuntime().exec(GPU_USAGE_COMMAND);
            InputStream processInputStream = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(processInputStream));
            gpuUsage = bufferedReader.readLine();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return Double.parseDouble(gpuUsage);
    }
}
