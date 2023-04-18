<#include "security.ftl">

<div class="card-columns">
    <#list messages as message>
    <div class="card my-3">
        <#if message.fileName??>
            <img src="/img/${message.fileName}" class="card-img-top">
        </#if>
        <div class="m-2">
            <span>${message.text}</span><br/>
            <i>#${message.tag}</i>
        </div>
        <div class="card-footer text-muted">
            <a href="/user-messages/${message.author.id}">${message.author.username}</a>
            <#if message.author.id == currentUserId>
                <a class="btn btn-primary" href="/user-messages/${message.author.id}?message=${message.id}">
                    Edit
                </a>
            </#if>
        </div>
    </div>
    <#else>
    No message
    </#list>
</div>