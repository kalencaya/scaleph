/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.sliew.scaleph.storage.configuration;

import cn.sliew.scaleph.common.util.SystemUtil;
import cn.sliew.scaleph.spring.storage.*;
import cn.sliew.scaleph.storage.util.HadoopUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.aliyun.oss.AliyunOSSFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@Configuration
public class FileSystemConfiguration {

    @Autowired
    private SystemUtil systemUtil;

    @Bean
    @ConditionalOnProperty(value = "file-system.type", havingValue = "local")
    public FileSystem localFileSystem(LocalFileSystemProperties localFileSystemProperties) throws IOException, URISyntaxException {
        org.apache.hadoop.conf.Configuration conf = HadoopUtil.getHadoopConfiguration(localFileSystemProperties.getHadoopConfPath());
        FileSystem fileSystem = FileSystem.getLocal(conf);
        setFsWorkingDirectory(fileSystem, systemUtil.getLocalStorageDir().toString());
        return fileSystem;
    }

    @Bean
    @ConditionalOnProperty(value = "file-system.type", havingValue = "s3")
    public FileSystem s3FileSystem(S3FileSystemProperties s3FileSystemProperties) throws URISyntaxException, IOException {
        org.apache.hadoop.conf.Configuration conf = HadoopUtil.getHadoopConfiguration(s3FileSystemProperties.getHadoopConfPath());
        conf.set("fs.s3a.endpoint", s3FileSystemProperties.getEndpoint());
        conf.set("fs.s3a.access.key", s3FileSystemProperties.getAccessKey());
        conf.set("fs.s3a.secret.key", s3FileSystemProperties.getSecretKey());
        conf.setBoolean("fs.s3a.path.style.access", true);
        URI uri = new URI(FileSystemType.S3.getSchema() + s3FileSystemProperties.getBucket());
        return FileSystem.get(uri, conf);
    }

    @Bean
    @ConditionalOnProperty(value = "file-system.type", havingValue = "oss")
    public FileSystem ossFileSystem(OSSFileSystemProperties ossFileSystemProperties) throws IOException, URISyntaxException {
        org.apache.hadoop.conf.Configuration conf = HadoopUtil.getHadoopConfiguration(ossFileSystemProperties.getHadoopConfPath());
        conf.set("fs.oss.endpoint", ossFileSystemProperties.getEndpoint());
        conf.set("fs.oss.accessKeyId", ossFileSystemProperties.getAccessKey());
        conf.set("fs.oss.accessKeySecret", ossFileSystemProperties.getSecretKey());
        URI uri = new URI(FileSystemType.OSS.getSchema() + ossFileSystemProperties.getBucket());
        final AliyunOSSFileSystem aliyunOSSFileSystem = new AliyunOSSFileSystem();
        aliyunOSSFileSystem.initialize(uri, conf);
        return aliyunOSSFileSystem;
    }

    @Bean
    @ConditionalOnProperty(value = "file-system.type", havingValue = "hdfs")
    public FileSystem hdfsFileSystem(HDFSFileSystemProperties hdfsFileSystemProperties) throws URISyntaxException, IOException {
        org.apache.hadoop.conf.Configuration conf = HadoopUtil.getHadoopConfiguration(hdfsFileSystemProperties.getHadoopConfPath());
        if (StringUtils.hasText(hdfsFileSystemProperties.getDefaultFS())) {
            conf.set("fs.defaultFS", hdfsFileSystemProperties.getDefaultFS());
        }
        return FileSystem.get(conf);
    }

    private void setFsWorkingDirectory(FileSystem fileSystem, String workingDirectory) {
        if (workingDirectory == null) {
            log.warn("Null working directory");
            return;
        }
        String path = null;
        try {
            URI uri = new URI(workingDirectory);
            path = uri.getRawPath();
        } catch (Exception e) {
            log.error("Error parsing working directory {}", workingDirectory);
        }
        if (path != null) {
            log.info("Set working directory to {}", path);
            fileSystem.setWorkingDirectory(new Path(path));
        }
    }

}
