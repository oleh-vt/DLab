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

from fabric.api import *
from fabric.contrib.files import exists
import logging
import argparse
import json
import sys
import os
from dlab.ssn_lib import *

parser = argparse.ArgumentParser()
parser.add_argument('--hostname', type=str, default='')
parser.add_argument('--keyfile', type=str, default='')
parser.add_argument('--additional_config', type=str, default='{"empty":"string"}')
args = parser.parse_args()

dlab_conf_dir=os.environ['ssn_dlab_path'] + 'conf/'
web_path = os.environ['ssn_dlab_path'] + 'webapp/lib/'
local_log_filename = "{}_UI.log".format(os.environ['request_id'])
local_log_filepath = "/logs/" + os.environ['resource'] +  "/" + local_log_filename
logging.basicConfig(format='%(levelname)-8s [%(asctime)s]  %(message)s',
                    level=logging.INFO,
                    filename=local_log_filepath)


def configure_mongo():
    try:
        if not exists("/lib/systemd/system/mongod.service"):
            local('scp -i {} /root/templates/mongod.service_template {}:/tmp/mongod.service'.format(args.keyfile, env.host_string))
            sudo('mv /tmp/mongod.service /lib/systemd/system/mongod.service')
        local('scp -i {} /root/templates/instance_shapes.lst {}:/tmp/instance_shapes.lst'.format(args.keyfile, env.host_string))
        sudo('mv /tmp/instance_shapes.lst ' + os.environ['ssn_dlab_path'] + 'tmp/')
        local('scp -i {} /root/scripts/configure_mongo.py {}:/tmp/configure_mongo.py'.format(args.keyfile, env.host_string))
        sudo('mv /tmp/configure_mongo.py ' + os.environ['ssn_dlab_path'] + 'tmp/')
        sudo('python ' + os.environ['ssn_dlab_path'] + 'tmp/configure_mongo.py --region {} --base_name {} --sg "{}" --vpc {} --subnet {}'.format(os.environ['creds_region'], os.environ['conf_service_base_name'], os.environ['creds_security_groups_ids'].replace(" ", ""), os.environ['creds_vpc_id'], os.environ['creds_subnet_id']))
        return True
    except:
        return False


##############
# Run script #
##############
if __name__ == "__main__":
    print "Configure connections"
    try:
        env['connection_attempts'] = 100
        env.key_filename = [args.keyfile]
        env.host_string = os.environ['general_os_user'] + '@' + args.hostname
        deeper_config = json.loads(args.additional_config)
    except:
        sys.exit(2)

    print "Installing Supervisor"
    if not ensure_supervisor():
        logging.error('Failed to install Supervisor')
        sys.exit(1)

    print "Installing MongoDB"
    if not ensure_mongo():
        logging.error('Failed to install MongoDB')
        sys.exit(1)

    print "Configuring MongoDB"
    if not configure_mongo():
        logging.error('MongoDB configuration script has failed.')
        sys.exit(1)

    sudo('echo DLAB_CONF_DIR={} >> /etc/profile'.format(dlab_conf_dir))
    sudo('echo export DLAB_CONF_DIR >> /etc/profile')

    print "Starting Self-Service(UI)"
    if not start_ss(args.keyfile, env.host_string):
        logging.error('Failed to start UI')
        sys.exit(1)

    sys.exit(0)
