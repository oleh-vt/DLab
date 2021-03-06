/***************************************************************************

Copyright (c) 2016, EPAM SYSTEMS INC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

****************************************************************************/

import { Output, Component, EventEmitter, ViewEncapsulation } from '@angular/core';

export class DropdownListModel {
  constructor(
    public type: string,
    public value: string,
    public index: number
  ) { }
}

@Component({
  moduleId: module.id,
  selector: 'dropdown-list',
  templateUrl: 'dropdown-list.component.html',
  styleUrls: ['../dropdowns.component.css'],
  encapsulation: ViewEncapsulation.None
})

export class DropdownListComponent {
  model: DropdownListModel;
  label: string;
  type: string;
  byField: string;
  listStructure: string;
  items: any;
  map: any;

  @Output() selectedItem: EventEmitter<{}> = new EventEmitter();

  public selectOptions($event: Event, value: any, index: number): void {
    this.label = this.map ? this.map[value] : value[this.byField];
    this.model.value = value;
    this.model.index = index;

    this.onUpdate();
    $event.preventDefault();
  }

  public setDefaultOptions(items: any, label: string, type: string, byField: string, structure: string, map?: any) {
    this.model = new DropdownListModel(type, '', 0);
    this.items = items;
    this.label = label;
    this.type = type;
    this.byField = byField;
    this.listStructure = structure;

    this.map = map;
  }

  private onUpdate(): void {
    this.selectedItem.emit({ model: this.model });
  }
}
