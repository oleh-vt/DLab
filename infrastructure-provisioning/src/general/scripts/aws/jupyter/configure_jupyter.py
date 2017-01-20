#!/usr/bin/python

# *****************************************************************************
#
# Copyright (c) 2016, EPAM SYSTEMS INC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# ******************************************************************************

import logging
import json
import sys
from dlab.fab import *
from dlab.aws_meta import *
from dlab.aws_actions import *
import os
import uuid


if __name__ == "__main__":
    # enable debug level for boto3
    logging.getLogger('botocore').setLevel(logging.DEBUG)
    logging.getLogger('boto3').setLevel(logging.DEBUG)

    instance_class = 'notebook'
    local_log_filename = "{}_{}_{}.log".format(os.environ['resource'], os.environ['edge_user_name'],
                                               os.environ['request_id'])
    local_log_filepath = "/logs/" + os.environ['resource'] + "/" + local_log_filename
    logging.basicConfig(format='%(levelname)-8s [%(asctime)s]  %(message)s',
                        level=logging.DEBUG,
                        filename=local_log_filepath)

    notebook_config = dict()
    notebook_config['uuid'] = str(uuid.uuid4())[:5]
    try:
        notebook_config['exploratory_name'] = os.environ['exploratory_name']
    except:
        notebook_config['exploratory_name'] = ''
    notebook_config['service_base_name'] = os.environ['conf_service_base_name']
    notebook_config['instance_type'] = os.environ['aws_notebook_instance_type']
    notebook_config['key_name'] = os.environ['conf_key_name']
    notebook_config['user_keyname'] = os.environ['edge_user_name']
    notebook_config['instance_name'] = os.environ['conf_service_base_name'] + "-" + os.environ[
        'edge_user_name'] + "-nb-" + notebook_config['exploratory_name'] + "-" + notebook_config['uuid']
    notebook_config['expected_ami_name'] = os.environ['conf_service_base_name'] + "-" + os.environ[
        'edge_user_name'] + '-' + os.environ['application'] + '-notebook-image'
    notebook_config['role_profile_name'] = os.environ['conf_service_base_name'].lower().replace('-', '_') + "-" + \
                                           os.environ['edge_user_name'] + "-nb-Profile"
    notebook_config['security_group_name'] = os.environ['conf_service_base_name'] + "-" + os.environ[
        'edge_user_name'] + "-nb-SG"
    notebook_config['tag_name'] = notebook_config['service_base_name'] + '-Tag'

    print 'Searching preconfigured images'
    ami_id = get_ami_id_by_name(notebook_config['expected_ami_name'], 'available')
    if ami_id != '':
        print 'Preconfigured image found. Using: ' + ami_id
        notebook_config['ami_id'] = ami_id
    else:
        notebook_config['ami_id'] = get_ami_id(os.environ['aws_' + os.environ['conf_os_family'] + '_ami_name'])
        print 'No preconfigured image found. Using default one: ' + notebook_config['ami_id']

    tag = {"Key": notebook_config['tag_name'],
           "Value": "{}-{}-subnet".format(notebook_config['service_base_name'], os.environ['edge_user_name'])}
    notebook_config['subnet_cidr'] = get_subnet_by_tag(tag)
    # generating variables regarding EDGE proxy on Notebook instance
    instance_hostname = get_instance_hostname(notebook_config['instance_name'])
    edge_instance_name = os.environ['conf_service_base_name'] + "-" + os.environ['edge_user_name'] + '-edge'
    edge_instance_hostname = get_instance_hostname(edge_instance_name)
    keyfile_name = "/root/keys/{}.pem".format(os.environ['conf_key_name'])

    # configuring proxy on Notebook instance
    try:
        logging.info('[CONFIGURE PROXY ON JUPYTER INSTANCE]')
        print '[CONFIGURE PROXY ON JUPYTER INSTANCE]'
        additional_config = {"proxy_host": edge_instance_hostname, "proxy_port": "3128"}
        params = "--hostname {} --instance_name {} --keyfile {} --additional_config '{}' --os_user {}"\
            .format(instance_hostname, notebook_config['instance_name'], keyfile_name, json.dumps(additional_config), os.environ['conf_os_user'])
        try:
            local("~/scripts/{}.py {}".format('configure_proxy', params))
        except:
            with open("/root/result.json", 'w') as result:
                res = {"error": "Failed to configure proxy", "conf": notebook_config}
                print json.dumps(res)
                result.write(json.dumps(res))
                raise Exception
    except:
        remove_ec2(notebook_config['tag_name'], notebook_config['instance_name'])
        sys.exit(1)

    # updating repositories & installing python packages
    try:
        logging.info('[INSTALLING PREREQUISITES TO JUPYTER NOTEBOOK INSTANCE]')
        print('[INSTALLING PREREQUISITES TO JUPYTER NOTEBOOK INSTANCE]')
        params = "--hostname {} --keyfile {} --user {}".format(instance_hostname, keyfile_name, os.environ['conf_os_user'])
        try:
            local("~/scripts/{}.py {}".format('install_prerequisites', params))
        except:
            with open("/root/result.json", 'w') as result:
                res = {"error": "Failed installing apps: apt & pip", "conf": notebook_config}
                print json.dumps(res)
                result.write(json.dumps(res))
                raise Exception
    except:
        remove_ec2(notebook_config['tag_name'], notebook_config['instance_name'])
        sys.exit(1)

    # installing and configuring jupiter and all dependencies
    try:
        logging.info('[CONFIGURE JUPYTER NOTEBOOK INSTANCE]')
        print '[CONFIGURE JUPYTER NOTEBOOK INSTANCE]'
        params = "--hostname {} --instance_name {} --keyfile {} --region {} --spark_version {} --hadoop_version {} --os_user {}".\
            format(instance_hostname, notebook_config['instance_name'], keyfile_name, os.environ['aws_region'], os.environ['notebook_spark_version'], os.environ['notebook_hadoop_version'], os.environ['conf_os_user'])
        try:
            local("~/scripts/{}.py {}".format('configure_jupyter_node', params))
        except:
            with open("/root/result.json", 'w') as result:
                res = {"error": "Failed to configure jupyter", "conf": notebook_config}
                print json.dumps(res)
                result.write(json.dumps(res))
                raise Exception
    except:
        remove_ec2(notebook_config['tag_name'], notebook_config['instance_name'])
        sys.exit(1)

    # installing python2 and python3 libs
    try:
        logging.info('[CONFIGURE JUPYTER ADDITIONS]')
        print '[CONFIGURE JUPYTER ADDITIONS]'
        params = "--hostname {} --keyfile {} --os_user {}"\
            .format(instance_hostname, keyfile_name, os.environ['conf_os_user'])
        try:
            local("~/scripts/{}.py {}".format('install_jupyter_additions', params))
        except:
            with open("/root/result.json", 'w') as result:
                res = {"error": "Failed to install python libs", "conf": notebook_config}
                print json.dumps(res)
                result.write(json.dumps(res))
                raise Exception
    except:
        remove_ec2(notebook_config['tag_name'], notebook_config['instance_name'])
        sys.exit(1)

    try:
        print '[INSTALLING USERs KEY]'
        logging.info('[INSTALLING USERs KEY]')
        additional_config = {"user_keyname": notebook_config['user_keyname'],
                             "user_keydir": "/root/keys/"}
        params = "--hostname {} --keyfile {} --additional_config '{}'".format(
            instance_hostname, keyfile_name, json.dumps(additional_config))
        try:
            local("~/scripts/{}.py {}".format('install_user_key', params))
        except:
            with open("/root/result.json", 'w') as result:
                res = {"error": "Failed installing users key", "conf": params}
                print json.dumps(res)
                result.write(json.dumps(res))
                raise Exception
    except:
        sys.exit(1)

    # checking the need for image creation
    ami_id = get_ami_id_by_name(notebook_config['expected_ami_name'])
    if ami_id == '':
        print "Looks like it's first time we configure notebook server. Creating image."
        image_id = create_image_from_instance(instance_name=notebook_config['instance_name'],
                                              image_name=notebook_config['expected_ami_name'])
        if image_id != '':
            print "Image was successfully created. It's ID is " + image_id

    # generating output information
    ip_address = get_instance_ip_address(notebook_config['instance_name']).get('Private')
    dns_name = get_instance_hostname(notebook_config['instance_name'])
    jupyter_ip_url = "http://" + ip_address + ":8888/" + notebook_config['instance_name'] + "/"
    jupyter_dns_url = "http://" + dns_name + ":8888/" + notebook_config['instance_name'] + "/"
    print '[SUMMARY]'
    logging.info('[SUMMARY]')
    print "Instance name: " + notebook_config['instance_name']
    print "Private DNS: " + dns_name
    print "Private IP: " + ip_address
    print "Instance type: " + notebook_config['instance_type']
    print "Key name: " + notebook_config['key_name']
    print "User key name: " + notebook_config['user_keyname']
    print "AMI name: " + notebook_config['expected_ami_name']
    print "Profile name: " + notebook_config['role_profile_name']
    print "SG name: " + notebook_config['security_group_name']
    print "Jupyter URL: " + jupyter_ip_url
    print "Jupyter URL: " + jupyter_dns_url
    print 'SSH access (from Edge node, via IP address): ssh -i ' + notebook_config[
        'key_name'] + '.pem ' + os.environ['conf_os_user'] + '@' + ip_address
    print 'SSH access (from Edge node, via FQDN): ssh -i ' + notebook_config['key_name'] + '.pem ' + \
          os.environ['conf_os_user'] + '@' + dns_name

    with open("/root/result.json", 'w') as result:
        res = {"hostname": dns_name,
               "ip": ip_address,
               "master_keyname": os.environ['conf_key_name'],
               "notebook_name": notebook_config['instance_name'],
               "Action": "Create new notebook server",
               "exploratory_url": [
                   {"description": "Jupyter",
                    "url": jupyter_ip_url}]}
        result.write(json.dumps(res))