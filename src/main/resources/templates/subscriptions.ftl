<#import "parts/common.ftl" as c>

<@c.page>
    <h3>${userChannel.username}</h3>
    <div>${type}</div>
    <#list users as user>
        <li class="list-group-item">
            <a href="/user-messages/${user.id}">${user.username}</a>
        </li>
    </#list>
</@c.page>